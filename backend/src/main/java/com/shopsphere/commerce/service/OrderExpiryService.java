package com.shopsphere.commerce.service;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.marketplace.domain.Inventory;
import com.shopsphere.marketplace.repository.InventoryMovementRepository;
import com.shopsphere.marketplace.repository.InventoryRepository;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderExpiryService {
 private final OrderRepository orders; private final SellerOrderRepository sellerOrders; private final OrderItemRepository items; private final InventoryRepository inventory; private final InventoryMovementRepository movements; private final PaymentRepository payments; private final OrderEventRepository events;
 public OrderExpiryService(OrderRepository orders,SellerOrderRepository sellerOrders,OrderItemRepository items,InventoryRepository inventory,InventoryMovementRepository movements,PaymentRepository payments,OrderEventRepository events){this.orders=orders;this.sellerOrders=sellerOrders;this.items=items;this.inventory=inventory;this.movements=movements;this.payments=payments;this.events=events;}
 @Scheduled(fixedDelayString="${app.orders.expiry-scan-ms:60000}") @Transactional public void expireDueOrders(){for(CustomerOrder order:orders.findDueOrdersForUpdate(Instant.now()))expire(order);}
 @Transactional public void expireById(java.util.UUID orderId){orders.findByIdForUpdate(orderId).ifPresent(this::expire);}
 private void expire(CustomerOrder order){if(!"PAYMENT_PENDING".equals(order.getStatus())||!order.isExpired(Instant.now()))return;for(SellerOrder sellerOrder:sellerOrders.findAllByOrder_Id(order.getId())){for(OrderItem item:items.findAllBySellerOrder_Id(sellerOrder.getId())){Inventory stock=inventory.findByListingIdForUpdate(item.getListing().getId()).orElseThrow();stock.release(item.getQuantity());movements.save(new com.shopsphere.marketplace.domain.InventoryMovement(stock,"RELEASED",item.getQuantity(),"Expired order "+order.getOrderNumber(),null));}sellerOrder.cancel();}payments.findByOrder_Id(order.getId()).ifPresent(Payment::cancel);order.cancel();events.save(new OrderEvent(order,"PAYMENT_PENDING","CANCELLED",null,"Payment window expired; reserved stock released"));}
}
