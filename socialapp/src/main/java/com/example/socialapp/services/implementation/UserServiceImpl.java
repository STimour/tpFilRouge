package com.example.socialapp.services.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.socialapp.dto.UserDto;
import com.example.socialapp.entity.User;
import com.example.socialapp.repository.UserRepository;
import com.example.socialapp.services.interfaces.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Enregistre un nouvel utilisateur à partir des données fournies dans le DTO.
     *
     * Le mot de passe contenu dans le DTO est encodé (hashé) avant la création de l'entité.
     *
     * @param dto le UserDto contenant les informations à enregistrer (par exemple username et password)
     * @return l'entité User telle qu'enregistrée par le repository :
     *         - le mot de passe est encodé,
     *         - les champs renseignés automatiquement par la persistance (par ex. id, timestamps) sont initialisés,
     *         - représente l'état stocké en base après l'appel à userRepository.save(...).
     * @throws IllegalArgumentException si le nom d'utilisateur (username) est déjà utilisé.
     */
    @Override
    public User register(UserDto dto) {
        // Vérifie si le username est déjà pris
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Encode (hash) le mot de passe avant d’enregistrer
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
                
                User saved = userRepository.save(user);

                // Vérification basique : save doit retourner une entité non nulle et avec un id généré
                if (saved == null || saved.getId() == null) {
                    throw new IllegalStateException("Échec de l'enregistrement de l'utilisateur");
                }

                // Optionnel : double-vérification via le repository
                if (!userRepository.existsById(saved.getId())) {
                    throw new IllegalStateException("L'utilisateur n'a pas été persisté en base");
                }

                // Ne pas retourner le mot de passe dans l'objet renvoyé
                saved.setPassword(null);
                return saved;
    }
}
