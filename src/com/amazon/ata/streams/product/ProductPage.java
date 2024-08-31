package com.amazon.ata.streams.product;

import com.amazon.ata.streams.product.types.PriceRangeOption;
import com.amazon.ata.streams.product.types.PrimeOption;
import com.amazon.ata.streams.product.types.ProductImagesV2;
import com.amazon.ata.streams.product.types.ProductV2;
import com.amazon.ata.streams.product.types.ShippingProgramEnum;
import com.amazon.ata.streams.product.types.SortByEnum;

import java.util.*;
import java.util.stream.Collectors;

import static com.amazon.ata.streams.product.types.SortByEnum.PRICE_HIGH_TO_LOW;
import static com.amazon.ata.streams.product.types.SortByEnum.PRICE_LOW_TO_HIGH;
import static com.amazon.ata.streams.product.types.SortByEnum.REWARD_HIGH_TO_LOW;
import static com.amazon.ata.streams.product.types.SortByEnum.REWARD_LOW_TO_HIGH;

public class ProductPage {

    /**
     * Tally Summary
     * Original score: 74
     * Par: 47
     * New score: 41
     * 6 below par!
    **/

    private static final String LOOK_VARIANT = "LOOK";

    private final ProductV2 productV2;

    private final Map<SortByEnum, Comparator<ProductV2>> comparatorForSortBy = createSortComparatorMap();

    public ProductPage(ProductV2 productV2) {
        this.productV2 = productV2;
    }

    public ProductV2 getProduct() {
        return productV2;
    }

    /**
     * Returns the first (winning) buying option from ProductV2.
     *
     * Golf score: 10
     * Par: 4
     * Your score: 4
     *
     * @return An Optional with the winning BuyingOption, or empty if none.
     */
    public Optional<ProductV2.BuyingOption> getFirstBuyingOption() {
        //List<ProductV2.BuyingOption> buyingOptions = productV2.buyingOptions();
        //if (!buyingOptions.isEmpty()) {
        //    return buyingOptions.stream()
        //        .findFirst();
        //}
        //return Optional.empty();

        return productV2.buyingOptions() // return and method : 2
                .stream() // method : 1
                .findFirst(); // method : 1
    }

    /**
     * Extracts the main image URL from the list of product images.
     *
     * As per https://api.corp.amazon.com/operations/TPvRr1W3vu/productImages picks the first image from the list
     * as the main image instead of using the "MAIN" image variant" which apparently is not necessarily the main image.
     *
     * Golf score: 18
     * Par: 8
     * Your score: 8
     *
     * @param longestDimension The size of the longest dimension of the image.
     * @return Optional containing the image URL, or empty if no image exists.
     */
    public Optional<String> extractMainImageUrl(Integer longestDimension) {
        /*
        Optional<ProductImagesV2> productImagesOptional = productV2.productImages();
        if (productImagesOptional.isPresent()) {
            ProductImagesV2 productImages = productImagesOptional.get();
            List<ProductImagesV2.Image> images = productImages.images();
            for (ProductImagesV2.Image image : images) {
                String url = extractImageUrl(image, longestDimension);
                if (url != null) {
                    return Optional.of(url);
                }
            }
        }

        return Optional.empty();
        */
        return productV2.productImages() // return and method : 2
                .map(ProductImagesV2::images) // method : 1
                .orElse(Collections.emptyList()) // method : 1
                .stream() // method : 1
                .map(image -> extractImageUrl(image, longestDimension)) // method : 1
                .filter(Objects::nonNull) // method : 1
                .findFirst(); // method : 1
    }

    /**
     * Extract image URL for LOOK variant if it exists.
     *
     * Golf score: 24
     * Par: 11
     * Your score: 11
     *
     * @param longestDimension the size of the image's longest dimension.
     * @return An Optional containing the URL of the image, or empty if no image exists.
     */
    public Optional<String> extractLookImageUrl(Integer longestDimension) {
        /*
        Optional<ProductImagesV2> productImages = productV2.productImages();
        if (productImages.isPresent()) {
            ProductImagesV2 productImagesV2 = productImages.get();
            List<ProductImagesV2.Image> images = productImagesV2.images();
            for (ProductImagesV2.Image image : images) {
                String variant = image.variant();
                if (variant != null && variant.equals(LOOK_VARIANT)) {
                    String url = extractImageUrl(image, longestDimension);
                    if (url != null) {
                        return Optional.of(url);
                    }
                }
            }
        }
        return Optional.empty();
        */

        return productV2.productImages() // return and method : 2
                .map(ProductImagesV2::images)// method : 1
                .orElse(Collections.emptyList()) // method : 1
                .stream()// method : 1
                .filter(Objects::nonNull) // method reference : 1
                .filter(image -> LOOK_VARIANT.equals(image.variant())) // method calls : 2
                .map(image -> extractImageUrl(image, longestDimension)) // method call : 1
                .filter(Objects::nonNull) // method reference : 1
                .findFirst(); // method call : 1
    }

    /**
     * Get products to display from AAPI.
     *
     * @param sortBy sort by parameter
     * @param priceRange price range filter
     * @param primeOption prime filter
     * @return list of products
     *
     * Golf score: 22
     * Par: 24
     * Your score: 18
     */
    public List<ProductV2> getSimilarProducts(final SortByEnum sortBy,
                                              final PriceRangeOption priceRange,
                                              final PrimeOption primeOption) {

        /*
        Comparator<ProductV2> sorter = comparatorForSortBy.getOrDefault(sortBy, passthroughComparator());
        final List<ProductV2> unorderedProducts = productV2.getSimilarProducts();
        final List<ProductV2> matchingProducts = new ArrayList<>();
        if (unorderedProducts != null) {
            for (ProductV2 product : unorderedProducts) {
                if (Objects.nonNull(product) &&
                    product.isValid() &&
                    priceRange.priceIsWithin(product.getPrice())) {
                    for (ShippingProgramEnum shippingProgram : product.getShippingPrograms()) {
                        if (primeOption.matches(shippingProgram)) {
                            matchingProducts.add(product);
                            break;
                        }
                    }
                }
            }
        }
        matchingProducts.sort(sorter);
        return matchingProducts;
        */

        return Optional.ofNullable(productV2.getSimilarProducts()) // return and 2 method calls : 3
                .orElse(Collections.emptyList())// method : 1
                .stream() // method call : 1
                .filter(Objects::nonNull) // method reference : 1
                .filter(ProductV2::isValid) // method reference : 1
                .filter(product -> priceRange.priceIsWithin(product.getPrice())) // 2 method calls : 2
                .filter(product -> product.getShippingPrograms().stream() // 2 method calls : 2
                        .anyMatch(primeOption::matches)) // method call and method reference : 2
                .sorted(comparatorForSortBy.getOrDefault(sortBy, passthroughComparator())) // 3 method calls : 3
                .collect(Collectors.toList()); // 2 methods : 2

    }

    /**
     * Extracts the image URL from a ProductImageV2.Image.
     */
    private String extractImageUrl(ProductImagesV2.Image image, Integer longest) {
        // Looks like a Stream or Optional, but it's a Builder.
        return image.lowRes().styleBuilder()
            .scaleToLongest(longest)
            .build()
            .url();
    }

    /**
     * Returns a Comparator that does not change order.
     * @param <T> The type of item this Comparator will compare.
     * @return a Comparator that does not change order.
     */
    private <T> Comparator<T> passthroughComparator() {
        return Comparator.comparing(other -> 0);
    }

    private Map<SortByEnum, Comparator<ProductV2>> createSortComparatorMap() {
        Map<SortByEnum, Comparator<ProductV2>> comparatorMap = new HashMap<>();
        comparatorMap.put(REWARD_LOW_TO_HIGH, Comparator.comparing(ProductV2::getTotalBenefitAmount));
        comparatorMap.put(REWARD_HIGH_TO_LOW, Comparator.comparing(ProductV2::getTotalBenefitAmount).reversed());
        comparatorMap.put(PRICE_LOW_TO_HIGH, Comparator.comparing(ProductV2::getPrice));
        comparatorMap.put(PRICE_HIGH_TO_LOW, Comparator.comparing(ProductV2::getPrice).reversed());
        return comparatorMap;
    }
}
