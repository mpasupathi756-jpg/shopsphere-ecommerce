package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.*;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.domain.Seller;
import com.shopsphere.marketplace.repository.SellerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class OrderQueryService {
 private final OrderRepository orders; private final SellerOrderRepository sellerOrders; private final OrderItemRepository items; private final SellerRepository sellers;
 public OrderQueryService(OrderRepository orders,SellerOrderRepository sellerOrders,OrderItemRepository items,SellerRepository sellers){this.orders=orders;this.sellerOrders=sellerOrders;this.items=items;this.sellers=sellers;}
 @Transactional(readOnly=true) public List<OrderSummaryResponse> customerOrders(UUID userId){return orders.findAllByCustomer_IdOrderByCreatedAtDesc(userId).stream().map(o->new OrderSummaryResponse(o.getOrderNumber(),o.getStatus(),o.getTotalMinor(),o.getCurrency(),o.getCreatedAt(),sellerOrders.findAllByOrder_Id(o.getId()).size())).toList();}
 @Transactional(readOnly=true) public OrderDetailResponse customerOrder(UUID userId,String orderNumber){CustomerOrder order=orders.findByOrderNumberAndCustomer_Id(orderNumber,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found"));return detail(order);}
 @Transactional(readOnly=true) public List<SellerOrderResponse> sellerOrders(UUID userId){Seller seller=sellers.findByUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Seller profile not found"));if(!"APPROVED".equals(seller.getStatus()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Seller account is not approved");return sellerOrders.findAllBySeller_IdOrderByCreatedAtDesc(seller.getId()).stream().map(this::sellerOrder).toList();}
 private OrderDetailResponse detail(CustomerOrder order){List<SellerOrderResponse> groups=sellerOrders.findAllByOrder_Id(order.getId()).stream().map(this::sellerOrder).toList();return new OrderDetailResponse(order.getOrderNumber(),order.getStatus(),order.getSubtotalMinor(),order.getShippingMinor(),order.getTotalMinor(),order.getCurrency(),order.getShippingRecipient(),order.getShippingPhone(),order.getShippingLine1(),order.getShippingLine2(),order.getShippingCity(),order.getShippingState(),order.getShippingPostalCode(),order.getShippingCountry(),order.getCreatedAt(),groups);}
 private SellerOrderResponse sellerOrder(SellerOrder sellerOrder){List<OrderItemResponse> lines=items.findAllBySellerOrder_Id(sellerOrder.getId()).stream().map(i->new OrderItemResponse(i.getProductTitleSnapshot(),i.getSellerSkuSnapshot(),i.getQuantity(),i.getUnitPriceMinor(),i.getLineTotalMinor(),i.getCurrency())).toList();return new SellerOrderResponse(sellerOrder.getId(),sellerOrder.getOrder().getOrderNumber(),sellerOrder.getSeller().getStoreName(),sellerOrder.getStatus(),sellerOrder.getSubtotalMinor(),sellerOrder.getTotalMinor(),sellerOrder.getCarrier(),sellerOrder.getTrackingNumber(),lines);}
}
