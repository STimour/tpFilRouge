package com.example.socialapp.config;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service utilitaire pour la création, la signature et l'extraction d'informations depuis des JSON Web Tokens (JWT).
 *
 * Description générale :
 * - Fournit des fonctions pour générer un JWT signé (HS256) contenant des claims supplémentaires et un subject (nom d'utilisateur),
 *   ainsi que des méthodes utilitaires pour extraire des claims ou le nom d'utilisateur depuis un token existant.
 * - Le token généré contient un instant d'émission (issuedAt) et une date d'expiration (ici configurée à 24 heures).
 *
 * Méthodes exposées (résumé fonctionnel) :
 * - generateToken(Map<String, Object> extraClaims, String username)
 *     Génère et renvoie un JWT compact :
 *     - Inclut les extraClaims fournis et définit le subject avec le username.
 *     - Définit issuedAt = maintenant et expiration = maintenant + 24h.
 *     - Signe le token avec la clé renvoyée par getSignInKey() en utilisant HS256.
 *
 * - extractUsername(String token)
 *     Extrait et renvoie le "subject" (nom d'utilisateur) du token en s'appuyant sur extractClaim.
 *
 * - <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
 *     Méthode générique pour extraire n'importe quel claim en appliquant un résolveur sur l'objet Claims décodé.
 *
 * - private Claims extractAllClaims(String token)
 *     Parse et valide le JWT signé (JWS) en vérifiant la signature à l'aide de la clé de signature.
 *     Retourne l'objet Claims (payload) si la validation réussit.
 *     - Peut jeter des exceptions runtime liées à la validation (p. ex. io.jsonwebtoken.JwtException)
 *       si le token est invalide, malformé ou expiré : l'appelant doit gérer ces cas.
 *
 * - private Key getSignInKey()
 *     Décodage de la constante SECRET_KEY (attendue en Base64) et construction d'une Key adaptée pour HMAC-SHA (Keys.hmacShaKeyFor).
 *
 * Remarques de sécurité et bonnes pratiques :
 * - Ne pas stocker la clé secrète en dur dans le code source (comme ici) :
 *     - Externaliser la clé dans un gestionnaire de secrets, variable d'environnement, ou keystore sécurisé.
 *     - Prévoir un mécanisme de rotation des clés.
 * - La clé utilisée pour HS256 doit être suffisamment longue (256 bits minimum) pour assurer la sécurité.
 * - Éviter de logger des tokens ou la clé secrète. Traiter les tokens comme des informations sensibles.
 * - Pour des scénarios à grande échelle ou multi-services, considérer l'utilisation d'une signature asymétrique (RS/ES)
 *   afin de séparer les rôles de signature et de vérification.
 *
 * Gestion des erreurs :
 * - Les opérations de parsing/validation peuvent lever des exceptions fournies par la bibliothèque JJWT
 *   (p. ex. ExpiredJwtException, MalformedJwtException, SignatureException, JwtException).
 * - L'appelant doit attraper et traiter ces exceptions (par ex. renvoyer une réponse 401/403 appropriée).
 *
 * Thread-safety :
 * - Ce service est stateless (aucun état mutable conservé entre appels) ; il est sûr d'être utilisé comme singleton Spring (@Service).
 *
 * Exemple d'utilisation (illustratif) :
 * - String token = jwtService.generateToken(Map.of("role", "USER"), "alice");
 * - String username = jwtService.extractUsername(token);
 *
 * @implNote remplacer la valeur en dur de SECRET_KEY par un stockage sécurisé (variables d'environnement, Vault, keystore, ...)
 * @since 1.0
 * @author GitHub Copilot
 */
@Service
public class JwtService {

    private static final String SECRET_KEY = "4f7e1ab48a8945d6a02b7e6a8b26f14f92a8e745b5194d1dbb2f02c25a8f9f2d"; // 256-bit secret

    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
