package com.shopsphere.marketplace.service;
import com.shopsphere.identity.domain.Role;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.identity.repository.RoleRepository;
import com.shopsphere.identity.repository.UserAccountRepository;
import com.shopsphere.marketplace.api.SellerApplicationRequest;
import com.shopsphere.marketplace.api.SellerApplicationResponse;
import com.shopsphere.marketplace.domain.Seller;
import com.shopsphere.marketplace.repository.SellerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class SellerService {
 private final SellerRepository sellers; private final UserAccountRepository users; private final RoleRepository roles;
 public SellerService(SellerRepository sellers,UserAccountRepository users,RoleRepository roles){this.sellers=sellers;this.users=users;this.roles=roles;}
 @Transactional public SellerApplicationResponse submit(UUID userId,SellerApplicationRequest request){UserAccount user=users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));if(!user.isActive())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account is not active");if(sellers.existsByUserId(userId))throw new ResponseStatusException(HttpStatus.CONFLICT,"A seller application already exists for this account");String gstin=request.gstin()==null||request.gstin().isBlank()?null:request.gstin().trim().toUpperCase(java.util.Locale.ROOT);Seller seller=sellers.save(new Seller(user,request.legalName().trim(),request.storeName().trim(),request.businessEmail().trim().toLowerCase(java.util.Locale.ROOT),request.phone().trim(),request.businessAddress().trim(),gstin));return toResponse(seller);}
 @Transactional(readOnly=true) public SellerApplicationResponse mine(UUID userId){Seller seller=sellers.findByUserId(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"No seller application found"));return toResponse(seller);}
 @Transactional(readOnly=true) public List<SellerApplicationResponse> list(String status){String normalized=status==null||status.isBlank()?"PENDING_REVIEW":status.toUpperCase(java.util.Locale.ROOT);if(!List.of("PENDING_REVIEW","APPROVED","REJECTED","SUSPENDED").contains(normalized))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported seller status");return sellers.findAllByStatusOrderBySubmittedAtDesc(normalized).stream().map(this::toResponse).toList();}
 @Transactional public SellerApplicationResponse approve(UUID sellerId,UUID adminId){Seller seller=sellers.findById(sellerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller application not found"));if(!"PENDING_REVIEW".equals(seller.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only pending seller applications can be approved");UserAccount admin=users.findById(adminId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Admin account not found"));Role sellerRole=roles.findByCode("SELLER").orElseThrow(()->new IllegalStateException("SELLER role is missing"));seller.approve(admin);seller.getUser().assignRole(sellerRole);users.save(seller.getUser());return toResponse(seller);}
 @Transactional public SellerApplicationResponse reject(UUID sellerId,UUID adminId,String reason){Seller seller=sellers.findById(sellerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Seller application not found"));if(!"PENDING_REVIEW".equals(seller.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only pending seller applications can be rejected");UserAccount admin=users.findById(adminId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Admin account not found"));seller.reject(admin,reason.trim());return toResponse(seller);}
 private SellerApplicationResponse toResponse(Seller seller){return new SellerApplicationResponse(seller.getId(),seller.getUser().getId(),seller.getStoreName(),seller.getLegalName(),seller.getBusinessEmail(),seller.getPhone(),seller.getStatus(),seller.getRejectionReason(),seller.getSubmittedAt(),seller.getReviewedAt());}
}
