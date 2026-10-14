package com.redstore

private val shirtSizes = listOf("S", "M", "L", "XL", "XXL")
private val shoeSizes = listOf("6", "7", "8", "9", "10", "11")
private val pantSizes = listOf("S", "M", "L", "XL")

val catalog: List<Product> = listOf(
    Product(
        id = 1,
        title = "Red Printed T-Shirt",
        brand = "HRX",
        category = "T-Shirts",
        price = 50.0,
        rating = 4.0,
        description = "Give your summer wardrobe a style upgrade with the HRX men's active tee. Pair it with shorts for a workout or denim for an evening out.",
        images = listOf(
            "images/gallery-1.jpg",
            "images/gallery-2.jpg",
            "images/gallery-3.jpg",
            "images/gallery-4.jpg",
            "images/product-1.jpg"
        ),
        sizes = shirtSizes,
        featured = true
    ),
    Product(
        id = 2,
        title = "HRX Sports Shoes",
        brand = "HRX",
        category = "Shoes",
        price = 75.0,
        rating = 4.5,
        description = "Lightweight training shoes with a cushioned sole and a grip that stays planted on the court.",
        images = listOf("images/product-2.jpg"),
        sizes = shoeSizes,
        featured = true
    ),
    Product(
        id = 3,
        title = "HRX Gray Trackpants",
        brand = "HRX",
        category = "Pants",
        price = 45.0,
        rating = 4.5,
        description = "Tapered trackpants in a soft stretch blend. Elastic waist, easy movement, all-day comfort.",
        images = listOf("images/product-3.jpg"),
        sizes = pantSizes,
        featured = true
    ),
    Product(
        id = 4,
        title = "Blue Printed T-Shirt",
        brand = "HRX",
        category = "T-Shirts",
        price = 55.0,
        rating = 4.0,
        description = "A breathable cotton tee with a bold blue print. Cut for a relaxed fit.",
        images = listOf("images/product-4.jpg"),
        sizes = shirtSizes,
        featured = true
    ),
    Product(
        id = 5,
        title = "Puma Gray Sports Shoes",
        brand = "Puma",
        category = "Shoes",
        price = 95.0,
        rating = 4.0,
        description = "Everyday runners in muted gray. Padded collar and a flexible outsole for city miles.",
        images = listOf("images/product-5.jpg"),
        sizes = shoeSizes,
        latest = true
    ),
    Product(
        id = 6,
        title = "Black Printed T-Shirt",
        brand = "HRX",
        category = "T-Shirts",
        price = 55.0,
        rating = 4.5,
        description = "A black graphic tee that works under a jacket or on its own.",
        images = listOf("images/product-6.jpg"),
        sizes = shirtSizes,
        latest = true
    ),
    Product(
        id = 7,
        title = "HRX Set of 3 Socks",
        brand = "HRX",
        category = "Accessories",
        price = 30.0,
        rating = 4.5,
        description = "Three-pack ankle socks with reinforced heels. Soft knit, stays up through a workout.",
        images = listOf("images/product-7.jpg"),
        sizes = listOf("Free"),
        latest = true
    ),
    Product(
        id = 8,
        title = "Black Fossil Watch",
        brand = "Fossil",
        category = "Watches",
        price = 120.0,
        rating = 4.0,
        description = "A matte black dial with a slim strap. Built for everyday wear.",
        images = listOf("images/product-8.jpg"),
        sizes = listOf("Standard"),
        latest = true
    ),
    Product(
        id = 9,
        title = "Black Sportx Watch",
        brand = "Sportx",
        category = "Watches",
        price = 135.0,
        rating = 4.0,
        description = "Sport watch with a high-contrast face and a strap that handles sweat and rain.",
        images = listOf("images/product-9.jpg"),
        sizes = listOf("Standard"),
        latest = true
    ),
    Product(
        id = 10,
        title = "Black HRX Shoes",
        brand = "HRX",
        category = "Shoes",
        price = 50.0,
        rating = 4.5,
        description = "All-black court shoes with a clean profile and a stable midsole.",
        images = listOf("images/product-10.jpg"),
        sizes = shoeSizes,
        latest = true
    ),
    Product(
        id = 11,
        title = "Gray Nike Shoes",
        brand = "Nike",
        category = "Shoes",
        price = 55.0,
        rating = 4.5,
        description = "Gray trainers with a soft insole. Easy to slip on for a commute or a run.",
        images = listOf("images/product-11.jpg"),
        sizes = shoeSizes,
        latest = true
    ),
    Product(
        id = 12,
        title = "HRX Black Trackpants",
        brand = "HRX",
        category = "Pants",
        price = 75.0,
        rating = 4.0,
        description = "Black trackpants with a tapered leg and zip pockets. Moves with you.",
        images = listOf("images/product-12.jpg"),
        sizes = pantSizes,
        latest = true
    ),
    Product(
        id = 13,
        title = "Smart Band 4",
        brand = "RedStore",
        category = "Watches",
        price = 49.0,
        rating = 4.5,
        description = "A full-touch color band with adjustable brightness, step tracking, and a strap light enough to forget you are wearing it.",
        images = listOf("images/exclusive.png"),
        sizes = listOf("Standard"),
        featured = true
    )
)

val categories = listOf(
    Category("Shoes", "images/category-1.jpg"),
    Category("T-Shirts", "images/category-2.jpg"),
    Category("Watches", "images/category-3.jpg"),
    Category("Pants", "images/product-3.jpg"),
    Category("Accessories", "images/product-7.jpg")
)

val testimonials = listOf(
    Testimonial(
        "Sean Parker",
        "The shoes arrived fast and the fit was exactly what the size chart promised.",
        "images/user-1.png",
        4.0
    ),
    Testimonial(
        "Mike Smith",
        "I keep coming back for the tees. Soft fabric, and the prints hold up after washing.",
        "images/user-2.png",
        5.0
    ),
    Testimonial(
        "Mabel Joe",
        "Checkout was simple and the trackpants are now my default for travel days.",
        "images/user-3.png",
        4.0
    )
)

val brandLogos = listOf(
    "images/logo-godrej.png",
    "images/logo-oppo.png",
    "images/logo-coca-cola.png",
    "images/logo-paypal.png",
    "images/logo-philips.png"
)

fun homePayload() = HomeResponse(
    heroImage = "images/image1.png",
    offerTitle = "Smart Band 4",
    offerText = "Exclusively on RedStore. A larger full-touch display with adjustable brightness, so every stat stays easy to read.",
    offerImage = "images/exclusive.png",
    offerProductId = 13,
    categories = categories,
    featured = catalog.filter { it.featured },
    latest = catalog.filter { it.latest },
    testimonials = testimonials,
    brands = brandLogos
)

fun findProduct(id: Int): Product =
    catalog.find { it.id == id } ?: throw ApiException(io.ktor.http.HttpStatusCode.NotFound, "Product not found")
