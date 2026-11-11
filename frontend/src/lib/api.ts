const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'
let accessToken: string | null = null
let csrfToken: string | null = null
let csrfFetchedAt = 0
export type UserProfile = { id: string; email: string; displayName: string; roles: string[]; emailVerified: boolean }
export type AuthResponse = { accessToken: string; tokenType: string; expiresIn: number; user: UserProfile }
export function setAccessToken(token: string | null) { accessToken = token }
async function ensureCsrf(): Promise<string> {
  if (csrfToken && Date.now() - csrfFetchedAt < 45 * 60 * 1000) return csrfToken
  const response = await fetch(`${API_BASE}/auth/csrf`, { credentials: 'include' })
  if (!response.ok) throw new Error('Unable to start a secure session. Please retry.')
  const data = await response.json() as { csrfToken: string }
  csrfToken = data.csrfToken
  csrfFetchedAt = Date.now()
  return csrfToken
}
async function send<T>(path: string, options: RequestInit = {}, retry = true): Promise<T> {
  const method = (options.method ?? 'GET').toUpperCase()
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  if (accessToken) headers.set('Authorization', `Bearer ${accessToken}`)
  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) headers.set('X-CSRF-TOKEN', await ensureCsrf())
  const response = await fetch(`${API_BASE}${path}`, { ...options, method, headers, credentials: 'include' })
  if (response.status === 401 && retry && !path.startsWith('/auth/')) {
    try { await restoreSession(); return send<T>(path, options, false) } catch { setAccessToken(null) }
  }
  if (!response.ok) {
    let message = 'Something went wrong. Please try again.'
    try { const data = await response.json() as { detail?: string; message?: string }; message = data.detail ?? data.message ?? message } catch { /* use fallback */ }
    throw new Error(message)
  }
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}
export async function restoreSession(): Promise<UserProfile> {
  const csrf = await ensureCsrf()
  const response = await fetch(`${API_BASE}/auth/refresh`, { method: 'POST', credentials: 'include', headers: { 'X-CSRF-TOKEN': csrf, 'Content-Type': 'application/json' }, body: '{}' })
  if (!response.ok) { setAccessToken(null); throw new Error('No active session') }
  const data = await response.json() as AuthResponse
  setAccessToken(data.accessToken)
  return data.user
}
export const authApi = {
  async register(input: { displayName: string; email: string; password: string }) { const data = await send<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(input) }, false); setAccessToken(data.accessToken); return data.user },
  async login(input: { email: string; password: string }) { const data = await send<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(input) }, false); setAccessToken(data.accessToken); return data.user },
  async logout() { try { await send<void>('/auth/logout', { method: 'POST', body: '{}' }, false) } finally { setAccessToken(null); csrfToken = null; csrfFetchedAt = 0 } },
  async me() { return send<UserProfile>('/auth/me') },
}

export type Category = { id: string; name: string; slug: string; description: string | null; displayOrder: number }
export type ProductCard = { id: string; title: string; slug: string; categoryName: string; brandName: string | null; startingPriceMinor: number; currency: string; sellerCount: number; primaryImageKey: string | null; averageRating: number | null; reviewCount: number }
export type ProductAttribute = { key: string; label: string; value: string; unit: string | null }
export type ProductImage = { storageKey: string; altText: string; primary: boolean }
export type ListingOffer = { listingId: string; sellerId: string; storeName: string; sellerSku: string; priceMinor: number; currency: string; condition: string; availableQuantity: number; shippingDaysMin: number; shippingDaysMax: number; returnWindowDays: number; warrantyText: string | null }
export type ProductDetail = { id: string; title: string; slug: string; description: string; categoryName: string; brandName: string | null; attributes: ProductAttribute[]; images: ProductImage[]; offers: ListingOffer[]; averageRating: number | null; reviewCount: number }
export type ProductPage = { content: ProductCard[]; totalElements: number; totalPages: number; number: number; size: number }
export type SellerApplication = { id: string; userId: string; storeName: string; legalName: string; businessEmail: string; phone: string; status: string; rejectionReason: string | null; submittedAt: string; reviewedAt: string | null }
export type SellerProduct = { id: string; title: string; slug: string; categoryName: string; status: string; rejectionReason: string | null }
export type SellerListing = { id: string; productId: string; productTitle: string; sellerSku: string; priceMinor: number; currency: string; status: string; availableQuantity: number }
export type AdminListing = { id: string; productId: string; productTitle: string; sellerId: string; storeName: string; sellerSku: string; priceMinor: number; currency: string; status: string; availableQuantity: number; rejectionReason: string | null }
export const catalogueApi = {
  categories: () => send<Category[]>('/catalog/categories'),
  search: (params: { q?: string; category?: string; brand?: string; page?: number; size?: number } = {}) => {
    const query = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => { if (value !== undefined && value !== '') query.set(key, String(value)) })
    return send<ProductPage>(`/catalog/products?${query.toString()}`)
  },
  product: (slug: string) => send<ProductDetail>(`/catalog/products/${encodeURIComponent(slug)}`),
}
export type ProductReview = { id: string; productSlug: string; authorName: string; rating: number; title: string; body: string; verifiedPurchase: boolean; createdAt: string }
export type ReviewPage = { content: ProductReview[]; totalElements: number; totalPages: number; number: number; size: number }
export type ReviewEligibility = { eligible: boolean; alreadyReviewed: boolean; message: string }
export const reviewsApi = {
  list: (slug: string, page = 0) => send<ReviewPage>(`/products/${encodeURIComponent(slug)}/reviews?page=${page}&size=10`),
  eligibility: (slug: string) => send<ReviewEligibility>(`/products/${encodeURIComponent(slug)}/reviews/eligibility`),
  create: (slug: string, input: { rating: number; title: string; body: string }) => send<ProductReview>(`/products/${encodeURIComponent(slug)}/reviews`, { method: 'POST', body: JSON.stringify(input) }),
  mine: (page = 0) => send<ReviewPage>(`/me/reviews?page=${page}&size=20`),
  edit: (id: string, input: { rating: number; title: string; body: string }) => send<ProductReview>(`/me/reviews/${id}`, { method: 'PATCH', body: JSON.stringify(input) }),
  remove: (id: string) => send<void>(`/me/reviews/${id}`, { method: 'DELETE' }),
  report: (id: string, input: { reasonCode: string; details?: string }) => send<void>(`/reviews/${id}/reports`, { method: 'POST', body: JSON.stringify(input) }),
}
export type ReviewReport = { id: string; reviewId: string; productTitle: string; reviewTitle: string; reporterName: string; reasonCode: string; details: string | null; status: string; createdAt: string }
export type ReviewReportPage = { content: ReviewReport[]; totalElements: number; totalPages: number; number: number; size: number }
export const adminReviewsApi = {
  reports: () => send<ReviewReportPage>('/admin/reviews/reports?page=0&size=20'),
  moderate: (reviewId: string, action: 'HIDDEN' | 'PUBLISHED' | 'REMOVED', reason: string) => send<void>(`/admin/reviews/${reviewId}/moderation`, { method: 'PATCH', body: JSON.stringify({ action, reason }) }),
  resolve: (reportId: string, action: 'DISMISSED' | 'ACTIONED', note?: string) => send<void>(`/admin/reviews/reports/${reportId}`, { method: 'PATCH', body: JSON.stringify({ action, note }) }),
}
export const sellerApi = {
  submitApplication: (input: { legalName: string; storeName: string; businessEmail: string; phone: string; businessAddress: string; gstin?: string }) => send<SellerApplication>('/seller-applications', { method: 'POST', body: JSON.stringify(input) }),
  myApplication: () => send<SellerApplication>('/seller-applications/me'),
  products: () => send<SellerProduct[]>('/seller/products'),
  submitProduct: (input: { categoryId: string; brandId?: string | null; title: string; description: string }) => send<SellerProduct>('/seller/products', { method: 'POST', body: JSON.stringify(input) }),
  listings: () => send<SellerListing[]>('/seller/listings'),
  createListing: (input: { productId: string; sellerSku: string; priceMinor: number; currency: 'INR'; condition: 'NEW' | 'REFURBISHED' | 'USED'; shippingDaysMin: number; shippingDaysMax: number; returnWindowDays: number; warrantyText?: string; availableQuantity: number }) => send<SellerListing>('/seller/listings', { method: 'POST', body: JSON.stringify(input) }),
  pauseListing: (id: string) => send<SellerListing>(`/seller/listings/${id}/pause`, { method: 'PATCH' }),
  orders: () => send<SellerOrder[]>('/seller/orders'),
  startProcessing: (id: string) => send<SellerOrder>(`/seller/orders/${encodeURIComponent(id)}/processing`, { method: 'PATCH' }),
  shipOrder: (id: string, carrier: string, trackingNumber: string) => send<SellerOrder>(`/seller/orders/${encodeURIComponent(id)}/ship`, { method: 'PATCH', body: JSON.stringify({ carrier, trackingNumber }) }),
  markDelivered: (id: string) => send<SellerOrder>(`/seller/orders/${encodeURIComponent(id)}/delivered`, { method: 'PATCH' }),
}
export const adminApi = {
  sellers: (status = 'PENDING_REVIEW') => send<SellerApplication[]>(`/admin/sellers?status=${status}`),
  approveSeller: (id: string) => send<SellerApplication>(`/admin/sellers/${id}/approve`, { method: 'PATCH' }),
  rejectSeller: (id: string, reason: string) => send<SellerApplication>(`/admin/sellers/${id}/reject`, { method: 'PATCH', body: JSON.stringify({ reason }) }),
  pendingProducts: () => send<SellerProduct[]>('/admin/catalog/products/pending'),
  approveProduct: (id: string) => send<SellerProduct>(`/admin/catalog/products/${id}/approve`, { method: 'PATCH' }),
  rejectProduct: (id: string, reason: string) => send<SellerProduct>(`/admin/catalog/products/${id}/reject`, { method: 'PATCH', body: JSON.stringify({ reason }) }),
  pendingListings: () => send<AdminListing[]>('/admin/catalog/listings/pending'),
  approveListing: (id: string) => send<AdminListing>(`/admin/catalog/listings/${id}/approve`, { method: 'PATCH' }),
  rejectListing: (id: string, reason: string) => send<AdminListing>(`/admin/catalog/listings/${id}/reject`, { method: 'PATCH', body: JSON.stringify({ reason }) }),
}

export type Address = { id: string; recipientName: string; phone: string; addressLine1: string; addressLine2: string | null; city: string; state: string; postalCode: string; country: string; defaultAddress: boolean }
export type CartItem = { id: string; listingId: string; productId: string; productTitle: string; productSlug: string; storeName: string; unitPriceMinor: number; currency: string; quantity: number; availableQuantity: number; lineTotalMinor: number }
export type Cart = { items: CartItem[]; itemCount: number; subtotalMinor: number; currency: string }
export type WishlistItem = { productId: string; title: string; slug: string; categoryName: string; startingPriceMinor: number; currency: string }
export type CheckoutPreview = { shippingAddress: Address; sellerOrders: { sellerId: string; storeName: string; items: { listingId: string; productTitle: string; sellerSku: string; quantity: number; unitPriceMinor: number; lineTotalMinor: number; currency: string }[]; subtotalMinor: number; shippingMinor: number; totalMinor: number }[]; subtotalMinor: number; shippingMinor: number; discountMinor: number; totalMinor: number; currency: string; paymentEnabled: boolean; paymentNotice: string }
export const cartApi = {
  get: () => send<Cart>('/cart'),
  add: (listingId: string, quantity = 1) => send<Cart>('/cart/items', { method: 'POST', body: JSON.stringify({ listingId, quantity }) }),
  update: (id: string, quantity: number) => send<Cart>('/cart/items/' + id, { method: 'PATCH', body: JSON.stringify({ quantity }) }),
  remove: (id: string) => send<Cart>('/cart/items/' + id, { method: 'DELETE' }),
}
export const wishlistApi = {
  get: () => send<WishlistItem[]>('/wishlist'),
  add: (productId: string) => send<WishlistItem[]>('/wishlist/products/' + productId, { method: 'POST' }),
  remove: (productId: string) => send<WishlistItem[]>('/wishlist/products/' + productId, { method: 'DELETE' }),
}
export const addressApi = {
  get: () => send<Address[]>('/addresses'),
  create: (input: Omit<Address,'id'|'defaultAddress'> & { defaultAddress: boolean }) => send<Address>('/addresses', { method: 'POST', body: JSON.stringify(input) }),
  setDefault: (id: string) => send<Address>('/addresses/' + id + '/default', { method: 'PATCH' }),
}

export type CheckoutOrder = { orderNumber: string; status: string; subtotalMinor: number; shippingMinor: number; totalMinor: number; paymentProvider: string; paymentStatus: string; checkoutUrl: string | null; expiresAt: string }
export type OrderSummary = { orderNumber: string; status: string; totalMinor: number; currency: string; createdAt: string; sellerOrderCount: number }
export type OrderLine = { productTitle: string; sellerSku: string; quantity: number; unitPriceMinor: number; lineTotalMinor: number; currency: string }
export type SellerOrder = { sellerOrderId: string; orderNumber: string; storeName: string; status: string; subtotalMinor: number; totalMinor: number; carrier: string | null; trackingNumber: string | null; items: OrderLine[] }
export type OrderDetail = { orderNumber: string; status: string; subtotalMinor: number; shippingMinor: number; totalMinor: number; currency: string; recipientName: string; phone: string; addressLine1: string; addressLine2: string | null; city: string; state: string; postalCode: string; country: string; createdAt: string; sellerOrders: SellerOrder[] }
export const checkoutApi = {
  preview: (addressId: string) => send<CheckoutPreview>('/checkout/preview', { method: 'POST', body: JSON.stringify({ addressId }) }),
  placeOrder: (addressId: string, idempotencyKey: string) => send<CheckoutOrder>('/checkout/orders', { method: 'POST', headers: { 'Idempotency-Key': idempotencyKey }, body: JSON.stringify({ addressId }) }),
}
export const paymentApi = {
  mockSuccess: (orderNumber: string) => send<OrderDetail>('/payments/mock/' + encodeURIComponent(orderNumber) + '/success', { method: 'POST' }),
}
export const ordersApi = {
  list: () => send<OrderSummary[]>('/orders'),
  detail: (orderNumber: string) => send<OrderDetail>('/orders/' + encodeURIComponent(orderNumber)),
}

export type AssistantChatResponse = { answer: string; model: string }
export const assistantApi = {
  chat: (message: string) => send<AssistantChatResponse>('/assistant/chat', { method: 'POST', body: JSON.stringify({ message }) }),
}
