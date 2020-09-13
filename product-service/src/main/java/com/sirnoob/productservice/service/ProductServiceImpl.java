package com.sirnoob.productservice.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.mapper.IProductMapper;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

  private final IMainCategoryService iMainCategoryService;
  private final ISubCategoryRepository iSubCategoryRepository;
  private final IProductRepository iProductRepository;
  private final IProductMapper iProductMapper;

  @Override
  public ProductResponse createProduct(ProductRequest productRequest) {

    MainCategory mainCategory = iMainCategoryService.getMainCategoryByName(productRequest.getMainCategoryName());

    Set<SubCategory> subCategories = getSubcategoriesByName(productRequest.getSubCategoriesNames());

    Product product = iProductMapper.mapProductRequestToProduct(productRequest, mainCategory, subCategories);

    product.setProductStatus("CREATED");
    product.setCreateAt(LocalDate.now());

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Override
  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName) {
    return iProductMapper
        .mapProductToProductResponse(iProductRepository.findByProductBarCodeOrProductName(productBarCode, productName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Product " + productName + " Not Found with Bar Code " + productBarCode)));
  }

  @Override
  public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {

    Product product = iProductRepository.findById(productId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Product Not Found with Id " + productId));

    product.setProductBarCode(productRequest.getProductBarCode());
    product.setProductName(productRequest.getProductName());
    product.setProductDescription(productRequest.getProductDescription());
    product.setProductStock(productRequest.getProductStock());
    product.setProductPrice(productRequest.getProductPrice());
    product.setMainCategory(iMainCategoryService.getMainCategoryByName(productRequest.getMainCategoryName()));
    product.setSubCategories(getSubcategoriesByName(productRequest.getSubCategoriesNames()));

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Transactional
  @Override
  public int updateProductStock(Long productBarCode, Integer quantity) {
    return iProductRepository.updateProductStockByProductBarCode(quantity, productBarCode);
  }

  @Override
  public ProductInvoiceResponse getProductForInvoiceResponse(Long productBarCode, String productName) {
    return iProductRepository.findProductForInvoice(productBarCode, productName)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Product " + productName + " With BarCode " + productBarCode + " NOT FOUND."));
  }

  @Override
  public void deleteProductById(Long productId) {
    iProductRepository.delete(iProductRepository.findById(productId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product NOT FOUND with ID " + productId)));
  }


  @Override
  public ProductView findProductViewByName(String productName) {
    return iProductMapper.mapProductToProductView(iProductRepository.findByProductName(productName).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found with Name " + productName)));
  }

  @Override
  public List<ProductListView> getProductListViewByName(String productName, int page) {
    return iProductRepository.listByName(productName, PageRequest.of(page, 25));
  }

  @Override
  public List<ProductListView> getPageOfProductListView(int page) {
    return iProductRepository.getAll(PageRequest.of(page, 25));
  }

  @Override
  public List<ProductListView> getProductListViewByMainCategory(String mainCategoryName, int page) {
    return iProductRepository.findByMainCategory(iMainCategoryService.getMainCategoryByName(mainCategoryName), PageRequest.of(page, 10));
  }

  @Override
  public Set<ProductListView> getProductListViewBySubCategory(String[] subCategoriesNames) {
    Set<ProductListView> products = new HashSet<>();
    for (SubCategory sc : getSubcategoriesByName(subCategoriesNames)) {
      products.add(iProductRepository.findBySubCategory(sc));
    }
    return products;
  }




  private Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {

    Set<SubCategory> subCategories = new HashSet<>();

    for (String sc : subCategoriesNames) {
      subCategories.add(iSubCategoryRepository.findBySubCategoryName(sc)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub Category " + sc + " Not Found")));
    }

    return subCategories;
  }

}
