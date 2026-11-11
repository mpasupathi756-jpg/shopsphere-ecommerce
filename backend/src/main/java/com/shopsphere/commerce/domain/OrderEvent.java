package com.shopsphere.commerce.domain;
import com.shopsphere.identity.domain.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
@Entity @Table(name="order_events")
public class OrderEvent {
 @Id @UuidGenerator private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private CustomerOrder order;
 @Column(name="from_status",length=24) private String fromStatus;
 @Column(name="to_status",nullable=false,length=24) private String toStatus;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_user_id") private UserAccount actor;
 @Column(length=300) private String note;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected OrderEvent(){}
 public OrderEvent(CustomerOrder order,String fromStatus,String toStatus,UserAccount actor,String note){this.order=order;this.fromStatus=fromStatus;this.toStatus=toStatus;this.actor=actor;this.note=note;this.createdAt=Instant.now();}
}
