package com.shop.bookshop.assemblers;

import com.shop.bookshop.controllers.PurchaseController;
import com.shop.bookshop.etities.Purchase;
import com.shop.bookshop.etities.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PurchaseModelAssembler implements RepresentationModelAssembler<Purchase, EntityModel<Purchase>> {
    @Override
    public EntityModel<Purchase> toModel(Purchase purchase) {
        EntityModel<Purchase> purchaseEntityModel = EntityModel.of(purchase,
                linkTo(methodOn(PurchaseController.class).purchaseById(purchase.getId())).withSelfRel(),
                linkTo(methodOn(PurchaseController.class).allPurchases()).withRel("purchases"));

        if(purchase.getStatus() == Status.IN_PROGRESS){
            purchaseEntityModel.add(linkTo(methodOn(PurchaseController.class).pay(purchase.getId())).withRel("pay"));

            purchaseEntityModel.add(linkTo(methodOn(PurchaseController.class).cancel(purchase.getId())).withRel("cancel"));
        }

        if(purchase.getStatus() == Status.PAID){
            purchaseEntityModel.add(linkTo(methodOn(PurchaseController.class).cancel(purchase.getId())).withRel("cancel"));
        }

        return purchaseEntityModel;
    }
}