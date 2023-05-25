package com.example.buoi6.service;

import com.example.buoi6.entity.Product;
import com.example.buoi6.respository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public List<Product> GetAll(){
        return (List<Product>) repo.findAll();
    }

    public void add(Product newProduct){
        repo.save(newProduct);
    }
    public void delete(Product deleteProduct){
        repo.delete(deleteProduct);
    }
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }
    public void edit(Product editedProduct) {
        Product find = getProductById(editedProduct.getId());

        find.setName(editedProduct.getName());
        find.setPrice(editedProduct.getPrice());

        if (editedProduct.getImage() != null) {
            find.setImage(editedProduct.getImage());
        }

        repo.save(find);
    }



}
