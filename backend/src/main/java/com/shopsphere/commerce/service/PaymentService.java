package com.shopsphere.commerce.service;
import com.shopsphere.commerce.domain.*;
import com.shopsphere.commerce.repository.*;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.domain.Inventory;
import com.shopsphere.marketplace.repository.InventoryMovementRepository;
import com.shopsphere.marketplace.repository.InventoryRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class PaymentService {
 private final OrderRepository orders; private final PaymentRepository payments; private final SellerOrderRepository sellerOrders; private final OrderItemRepository items; private final InventoryRepository inventory; private final InventoryMovementRepository movements; private final OrderEventRepository events; private final OrderExpiryService expiry; private final OrderQueryService queries;
 public PaymentService(OrderRepository orders,PaymentRepository payments,SellerOrderRepository sellerOrders,OrderItemRepository items,InventoryRepository inventory,InventoryMovementRepository movements,OrderEventRepository events,OrderExpiryService expiry,OrderQueryService queries){this.orders=orders;this.payments=payments;this.sellerOrders=sellerOrders;this.items=items;this.inventory=inventory;this.movements=movements;this.events=events;this.expiry=expiry;this.queries=queries;}
 @Transactional public com.shopsphere.commerce.api.OrderDetailResponse confirmMockSuccess(UUID userId,String orderNumber){CustomerOrder order=orders.findByOrderNumberForUpdate(orderNumber,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found"));if("PAID".equals(order.getStatus()))return queries.customerOrder(userId,orderNumber);if(!"PAYMENT_PENDING".equals(order.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Order is not awaiting payment");if(order.isExpired(Instant.now())){expiry.expireById(order.getId());return queries.customerOrder(userId,orderNumber);}Payment payment=payments.findByOrder_Id(order.getId()).orElseThrow(()->new IllegalStateException("Payment record missing"));if(!"PENDING".equals(payment.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Payment is not pending");payment.markCaptured();order.markPaid();for(SellerOrder sellerOrder:sellerOrders.findAllByOrder_Id(order.getId())){sellerOrder.markPaid();for(OrderItem item:items.findAllBySellerOrder_Id(sellerOrder.getId())){Inventory stock=inventory.findByListingIdForUpdate(item.getListing().getId()).orElseThrow();stock.completeReservation(item.getQuantity());movements.save(new com.shopsphere.marketplace.domain.InventoryMovement(stock,"SOLD",0,"Payment captured for order "+orderNumber,null));}}events.save(new OrderEvent(order,"PAYMENT_PENDING","PAID",order.getCustomer(),"Development mock payment confirmed"));return queries.customerOrder(userId,orderNumber);}
}
