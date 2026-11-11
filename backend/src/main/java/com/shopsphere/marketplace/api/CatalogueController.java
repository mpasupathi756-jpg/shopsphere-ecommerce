package com.shopsphere.marketplace.api;
import com.shopsphere.marketplace.service.CatalogueService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController @Validated @RequestMapping("/api/v1/catalog")
public class CatalogueController {
 private final CatalogueService service;
 public CatalogueController(CatalogueService service){this.service=service;}
 @GetMapping("/categories") public List<CategoryResponse> categories(){return service.categories();}
 @GetMapping("/products") public Page<ProductCardResponse> search(@RequestParam(required=false) String q,@RequestParam(required=false) String category,@RequestParam(required=false) String brand,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="12") @Min(1) @Max(24) int size){return service.search(q,category,brand,page,size);}
 @GetMapping("/products/{slug}") public ProductDetailResponse detail(@PathVariable String slug){return service.detail(slug);}
}
