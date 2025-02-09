CREATE TABLE public.timings (
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  vendor_id CHAR(36) NULL,
  day VARCHAR(20) NOT NULL,
  open_time VARCHAR(20) NOT NULL,
  close_time VARCHAR(20) NOT NULL,
  CONSTRAINT timings_pkey PRIMARY KEY (id ASC),
  CONSTRAINT timings_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
) LOCALITY REGIONAL BY TABLE IN PRIMARY REGION

CREATE TABLE public.menu_items (
  id INT8 NOT NULL DEFAULT unique_rowid(),
  vendor_id CHAR(36) NULL,
  name VARCHAR(255) NOT NULL,
  description STRING NOT NULL,
  image_url STRING NOT NULL,
  price DECIMAL NOT NULL,
  category VARCHAR(255) NOT NULL,
  item_type VARCHAR(255) NOT NULL,
  contains_dairy BOOL NOT NULL,
  CONSTRAINT menu_items_pkey PRIMARY KEY (id ASC),
  CONSTRAINT menu_items_vendor_id_fkey FOREIGN KEY (vendor_id) REFERENCES public.vendors(id) ON DELETE CASCADE
) LOCALITY REGIONAL BY TABLE IN PRIMARY REGION

CREATE TABLE public.vendors (
  id CHAR(36) NOT NULL DEFAULT gen_random_uuid(),
  restaurant_name VARCHAR(255) NOT NULL,
  restaurant_address STRING NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  alternate_number VARCHAR(20) NULL,
  email VARCHAR(255) NOT NULL,
  password STRING NOT NULL,
  owner_name VARCHAR(255) NOT NULL,
  owner_phone_number VARCHAR(20) NOT NULL,
  owner_email VARCHAR(255) NOT NULL,
  receive_updates_on_whatsapp BOOL NULL,
  fssai_license VARCHAR(255) NOT NULL,
  fssai_url STRING NOT NULL,
  gstin_number VARCHAR(255) NOT NULL,
  gstin_url STRING NOT NULL,
  pan_number VARCHAR(255) NOT NULL,
  pan_url STRING NOT NULL,
  bank_account_number VARCHAR(255) NOT NULL,
  bank_account_name VARCHAR(255) NOT NULL,
  image_urls STRING[] NOT NULL,
  logo_url STRING NOT NULL,
  approved BOOL NULL DEFAULT false,
  created_at TIMESTAMP NULL DEFAULT current_timestamp():::TIMESTAMP,
  CONSTRAINT vendors_pkey PRIMARY KEY (id ASC)
) LOCALITY REGIONAL BY TABLE IN PRIMARY REGION

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
  bankAccountName STRING NOT NULL
);

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