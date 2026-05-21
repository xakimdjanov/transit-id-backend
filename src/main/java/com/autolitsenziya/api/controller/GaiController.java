package com.autolitsenziya.api.controller;

import com.autolitsenziya.api.entity.Role;
import com.autolitsenziya.api.entity.User;
import com.autolitsenziya.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gai")
@CrossOrigin(origins = "*")
public class GaiController {

    private final UserRepository userRepository;

    public GaiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllGai() {
        List<Map<String, Object>> gaiUsers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.GAI)
                .map(user -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id",        user.getId().toString());
                    map.put("phone",     user.getPhone());
                    map.put("role",      user.getRole().name());
                    map.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(gaiUsers);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createGai(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Telefon raqami kiritilishi shart!"));
        }

        phone = phone.trim();

        if (userRepository.existsByPhone(phone)) {
            User user = userRepository.findByPhone(phone).get();
            if (user.getRole() == Role.GAI) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Ushbu telefon raqamli GAI foydalanuvchisi allaqachon mavjud!"));
            }
            user.setRole(Role.GAI);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Foydalanuvchi roli GAI ga o'zgartirildi!"));
        }

        User gaiUser = User.builder()
                .phone(phone)
                .role(Role.GAI)
                .build();
        userRepository.save(gaiUser);

        return ResponseEntity.ok(Map.of("success", true, "message", "GAI foydalanuvchisi muvaffaqiyatli qo'shildi!"));
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<?> deleteGai(@PathVariable String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    if (user.getRole() != Role.GAI) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Ushbu foydalanuvchi GAI emas!"));
                    }
                    userRepository.delete(user);
                    return ResponseEntity.ok(Map.of("success", true, "message", "GAI foydalanuvchisi muvaffaqiyatli o'chirildi!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
