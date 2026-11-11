package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.SellerOrderResponse;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.identity.domain.UserAccount;
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
public class SellerOrderService {
 private final SellerRepository sellers; private final SellerOrderRepository sellerOrders; private final OrderItemRepository items; private final OrderEventRepository events; private final UserAccountRepository users; private final OrderQueryService queries;
 public SellerOrderService(SellerRepository sellers,SellerOrderRepository sellerOrders,OrderItemRepository items,OrderEventRepository events,UserAccountRepository users,OrderQueryService queries){this.sellers=sellers;this.sellerOrders=sellerOrders;this.items=items;this.events=events;this.users=users;this.queries=queries;}
 @Transactional(readOnly=true) public List<SellerOrderResponse> list(UUID userId){return queries.sellerOrders(userId);}
 @Transactional public SellerOrderResponse processing(UUID userId,UUID sellerOrderId){Seller seller=approvedSeller(userId);SellerOrder sellerOrder=sellerOrders.findByIdAndSeller_Id(sellerOrderId,seller.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller order not found"));String from=sellerOrder.getOrder().getStatus();try{sellerOrder.startProcessing();}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}CustomerOrder order=sellerOrder.getOrder();if("PAID".equals(order.getStatus())){order.markProcessing();events.save(new OrderEvent(order,from,"PROCESSING",seller.getUser(),"Seller started fulfilment"));}return queries.sellerOrders(userId).stream().filter(x->x.sellerOrderId().equals(sellerOrderId)).findFirst().orElseThrow();}
 @Transactional public SellerOrderResponse ship(UUID userId,UUID sellerOrderId,String carrier,String tracking){Seller seller=approvedSeller(userId);SellerOrder sellerOrder=sellerOrders.findByIdAndSeller_Id(sellerOrderId,seller.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller order not found"));try{sellerOrder.ship(carrier.trim(),tracking.trim());}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}CustomerOrder order=sellerOrder.getOrder();List<SellerOrder> all=sellerOrders.findAllByOrder_Id(order.getId());if(all.stream().allMatch(x->List.of("SHIPPED","DELIVERED").contains(x.getStatus()))){String from=order.getStatus();if("PROCESSING".equals(from)||"PAID".equals(from)){order.markShipped();events.save(new OrderEvent(order,from,"SHIPPED",seller.getUser(),"All seller shipments are dispatched"));}}return queries.sellerOrders(userId).stream().filter(x->x.sellerOrderId().equals(sellerOrderId)).findFirst().orElseThrow();}
 @Transactional public SellerOrderResponse delivered(UUID userId,UUID sellerOrderId){Seller seller=approvedSeller(userId);SellerOrder sellerOrder=sellerOrders.findByIdAndSeller_Id(sellerOrderId,seller.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller order not found"));try{sellerOrder.deliver();}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}CustomerOrder order=sellerOrder.getOrder();List<SellerOrder> all=sellerOrders.findAllByOrder_Id(order.getId());if(all.stream().allMatch(x->"DELIVERED".equals(x.getStatus()))){String from=order.getStatus();order.markDelivered();events.save(new OrderEvent(order,from,"DELIVERED",seller.getUser(),"All seller shipments delivered"));}return queries.sellerOrders(userId).stream().filter(x->x.sellerOrderId().equals(sellerOrderId)).findFirst().orElseThrow();}
 private Seller approvedSeller(UUID userId){Seller seller=sellers.findByUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Seller profile not found"));if(!"APPROVED".equals(seller.getStatus()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Seller is not approved");return seller;}
}
