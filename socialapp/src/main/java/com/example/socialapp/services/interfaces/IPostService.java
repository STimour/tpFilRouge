package com.example.socialapp.services.interfaces;

import org.springframework.data.domain.Page;

import com.example.socialapp.dto.PostDto;
import com.example.socialapp.entity.Post;

public interface IPostService {

    /**
    * Crée un nouveau post à partir des données fournies et l'associe à l'utilisateur indiqué.
    *
    * Préconditions :
    * - dto ne doit pas être null.
    * - username ne doit pas être null ni vide.
    *
    * Comportement attendu :
    * - Transforme le PostDto en entité Post, effectue les validations nécessaires,
    *   persiste l'entité et remplit les champs générés (id, timestamps, ...).
    *
    * @param dto      objet de transfert contenant les données du post (titre, contenu, etc.), non null
    * @param username nom d'utilisateur de l'auteur du post, non null et non vide
    * @return l'entité Post persistée résultante, avec les champs générés renseignés
    * @throws IllegalArgumentException si dto est null ou si username est null/empty
    * @throws RuntimeException         si la création en base échoue pour une raison interne
    */
    Post createPost(PostDto dto, String username);

    /**
    * Récupère une page de posts triés selon l'implémentation (ex. par date de création).
    *
    * Remarques :
    * - Les index de page sont zero-based (0 correspond à la première page).
    * - La méthode doit retourner un Page non null ; la page peut être vide si aucun élément.
    *
    * @param page index de la page à récupérer (zero-based), doit être >= 0
    * @param size nombre d'éléments par page, doit être > 0
    * @return Page contenant les entités Post correspondant à la page demandée ; ne doit pas être null
    * @throws IllegalArgumentException si page < 0 ou size <= 0
    */
    Page<Post> getAllPosts(int page, int size);

    /**
    * Enregistre un "like" sur le post identifié par postId et retourne l'entité mise à jour.
    *
    * Comportement possible :
    * - Incrémentation d'un compteur de likes,
    * - Ou enregistrement d'une relation utilisateur↔post,
    * - Ou basculement entre like/unlike selon l'implémentation (documenter le choix dans l'implémentation).
    *
    * Préconditions :
    * - postId ne doit pas être null.
    *
    * @param postId identifiant du post à liker, non null
    * @return le Post mis à jour après l'opération de like
    * @throws IllegalArgumentException        si postId est null
    * @throws java.util.NoSuchElementException si aucun post correspondant à postId n'est trouvé
    */
    Post likePost(Long postId);
}
