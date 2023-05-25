package com.example.buoi6.controller;

import com.example.buoi6.entity.Product;
import com.example.buoi6.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public String index(Model model){
        model.addAttribute("listproduct", productService.GetAll());
        return "products/index";
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("product", new Product());
        return "products/create";
    }

    @PostMapping("/create")
    public String create(@Valid Product newProduct, @RequestParam MultipartFile imageProduct, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("product", newProduct);
            return "products/create";
        }
        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Lưu trữ đường dẫn hình ảnh vào cơ sở dữ liệu
                String imagePath =  newImageFile;
                newProduct.setImage(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productService.add(newProduct);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") int id, Model model){
        Product find =  productService.GetAll().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        productService.delete(find);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") int id, Model model){
        model.addAttribute("product",  productService.getProductById(id));
        return "products/edit";

    }
    @PostMapping("/edit")
    public String edit(@Valid Product editProduct, @RequestParam MultipartFile imageProduct, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("product", editProduct);
            return "products/edit";
        }
        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                String imagePath =  newImageFile;
                editProduct.setImage(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productService.edit(editProduct);
        return "redirect:/products";
    }
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword ,Model model){
        List<Product> find = new ArrayList<>();
        if(keyword != ""){
            find =  productService.GetAll().stream().filter(p->p.getName().toLowerCase().contains(keyword)).toList();
        }else{
            find = productService.GetAll();
        }
        model.addAttribute("listproduct", find);
        return "products/index";
    }
}
