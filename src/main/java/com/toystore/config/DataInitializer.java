package com.toystore.config;

import com.toystore.model.Product;
import com.toystore.model.User;
import com.toystore.repository.ProductRepository;
import com.toystore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@toylandindia.com")) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@toylandindia.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(User.Role.ADMIN)
                    .build());
            System.out.println("✅ Admin user created: admin@toylandindia.com / Admin@123");
        }

        // Seed products if none exist
        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                Product.builder().name("Wooden Building Blocks").description("Educational colorful blocks for toddlers. Develops motor skills.").price(BigDecimal.valueOf(349)).originalPrice(BigDecimal.valueOf(499)).category("educational").emoji("🧱").stock(50).featured(true).rating(4.8).reviewCount(120).build(),
                Product.builder().name("Remote Control Car").description("Fast RC car with 4-wheel drive. 2.4GHz remote, 30m range.").price(BigDecimal.valueOf(799)).originalPrice(BigDecimal.valueOf(1199)).category("action").emoji("🚗").stock(30).featured(true).rating(4.6).reviewCount(89).build(),
                Product.builder().name("Puzzle Set 100 pcs").description("Colorful animal puzzle. Improves concentration and memory.").price(BigDecimal.valueOf(249)).originalPrice(BigDecimal.valueOf(399)).category("puzzles").emoji("🧩").stock(75).featured(true).rating(4.7).reviewCount(200).build(),
                Product.builder().name("Stuffed Teddy Bear").description("Super soft premium teddy bear. Safe for all ages.").price(BigDecimal.valueOf(449)).originalPrice(BigDecimal.valueOf(599)).category("dolls").emoji("🧸").stock(60).featured(true).rating(4.9).reviewCount(315).build(),
                Product.builder().name("Play Kitchen Set").description("Realistic kitchen with 20+ accessories. Promotes creativity.").price(BigDecimal.valueOf(1299)).originalPrice(BigDecimal.valueOf(1799)).category("creative").emoji("🍳").stock(20).featured(true).rating(4.5).reviewCount(67).build(),
                Product.builder().name("Art & Craft Kit").description("Complete art set with colors, brushes, canvas and more.").price(BigDecimal.valueOf(399)).originalPrice(BigDecimal.valueOf(549)).category("creative").emoji("🎨").stock(40).featured(true).rating(4.6).reviewCount(145).build(),
                Product.builder().name("Ludo Board Game").description("Classic family board game. 2-4 players.").price(BigDecimal.valueOf(199)).originalPrice(BigDecimal.valueOf(299)).category("educational").emoji("🎲").stock(100).featured(true).rating(4.4).reviewCount(88).build(),
                Product.builder().name("Science Kit for Kids").description("Fun experiments kit. Learn science through play!").price(BigDecimal.valueOf(699)).originalPrice(BigDecimal.valueOf(999)).category("educational").emoji("🔬").stock(25).featured(true).rating(4.7).reviewCount(52).build(),
                Product.builder().name("Football").description("Size 4 rubber football. Perfect for outdoor play.").price(BigDecimal.valueOf(299)).originalPrice(BigDecimal.valueOf(449)).category("outdoor").emoji("⚽").stock(80).featured(false).rating(4.5).reviewCount(190).build(),
                Product.builder().name("Badminton Set").description("Set of 2 rackets + 3 shuttlecocks. Indoor/outdoor use.").price(BigDecimal.valueOf(449)).originalPrice(BigDecimal.valueOf(649)).category("outdoor").emoji("🏸").stock(35).featured(false).rating(4.3).reviewCount(76).build(),
                Product.builder().name("Barbie Doll Set").description("Fashion doll with 5 outfits and accessories.").price(BigDecimal.valueOf(599)).originalPrice(BigDecimal.valueOf(849)).category("dolls").emoji("🪆").stock(45).featured(false).rating(4.8).reviewCount(234).build(),
                Product.builder().name("LEGO Classic Bricks").description("200 piece colorful LEGO set. Unlimited creations.").price(BigDecimal.valueOf(899)).originalPrice(BigDecimal.valueOf(1299)).category("creative").emoji("🏗️").stock(30).featured(false).rating(4.9).reviewCount(410).build()
            );

            productRepository.saveAll(products);
            System.out.println("✅ Sample products seeded: " + products.size() + " products");
        }
    }
}
