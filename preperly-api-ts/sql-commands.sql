
-- register done
create table restaurantImages(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  vendor_id CHAR(36) NOT NULL,
  imageUrls STRING[] NOT NULL,
  logoUrl STRING NOT NULL,
  CONSTRAINT restaurantImages_pkey PRIMARY KEY (id ASC),
  CONSTRAINT restaurantImages_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
)

create table vendors(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  restaurantName STRING NOT NULL,
  restaurantAddress STRING NOT NULL,
  phoneNumber STRING NOT NULL,
  alternateNumber STRING NULL,
  email STRING NOT NULL unique,
  password STRING NOT NULL,
  ownerName STRING NOT NULL,
  ownerPhoneNumber STRING NOT NULL,
  ownerEmail STRING NOT NULL,
  receiveUpdatesOnWhatsapp BOOL NULL DEFAULT false,
  approved BOOL NULL DEFAULT false,
  createdAt TIMESTAMP NULL DEFAULT current_timestamp():::TIMESTAMP,
  CONSTRAINT vendors_pkey PRIMARY KEY (id ASC)
)

create table timings(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  vendor_id CHAR(36) NOT NULL,
  day STRING NOT NULL,
  openTime STRING NOT NULL,
  closeTime STRING NOT NULL,
  CONSTRAINT timings_pkey PRIMARY KEY (id ASC),
  CONSTRAINT timings_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
);

create TABLE menuItems(
  id INT8 NOT NULL DEFAULT unique_rowid(),
  vendor_id CHAR(36) NOT NULL,
  name STRING NOT NULL,
  description STRING NOT NULL,
  imageUrl STRING NOT NULL,
  price DECIMAL NOT NULL,
  category STRING NOT NULL,
  itemType STRING NOT NULL,
  containsDairy BOOL NOT NULL,
  CONSTRAINT menuItems_pkey PRIMARY KEY (id ASC),
  CONSTRAINT menuItems_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
)

create table documents(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  vendor_id CHAR(36) NOT NULL,
  fssaiLicense STRING NOT NULL unique,
  fssaiUrl STRING NOT NULL,
  gstinNumber STRING NOT NULL unique,
  gstinUrl STRING NOT NULL,
  panNumber STRING NOT NULL unique,
  panUrl STRING NOT NULL,
  bankAccountNumber STRING NOT NULL unique,
  bankAccountName STRING NOT NULL,
  CONSTRAINT documents_pkey PRIMARY KEY (id ASC),
  CONSTRAINT documents_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
)

-- create customer
create table customers(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  name STRING NOT NULL,
  email STRING NOT NULL unique,
  phoneNumber STRING NOT NULL,
  password STRING NOT NULL,
  createdAt TIMESTAMP NULL DEFAULT current_timestamp():::TIMESTAMP,
  CONSTRAINT customers_pkey PRIMARY KEY (id ASC)
)

-- create order
create table orders(
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  customer_id CHAR(36) NOT NULL,
  vendor_id CHAR(36) NOT NULL,
  items JSONB NOT NULL,
  totalAmount DECIMAL NOT NULL,
  totalQuantity INT8 NOT NULL,
  orderStatus order_status NOT NULL,
  arrivaTime TIMESTAMP NOT NULL,
  orderType order_type NOT NULL,
  CONSTRAINT orders_pkey PRIMARY KEY (id ASC),
  CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id) ON DELETE CASCADE,
  CONSTRAINT orders_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
)