import { z } from 'zod'

export const contactFormSchema = z.object({
  name: z
    .string()
    .min(2, { message: 'Name must be at least 2 characters' })
    .max(32, { message: 'Name must be at most 32 characters' }),
  email: z.string().email({ message: 'Invalid email address' }),
  message: z
    .string()
    .min(2, { message: 'Message must be at least 2 characters' })
    .max(1000, { message: 'Message must be at most 1000 characters' }),
})

export const fileUploadSchema = z.object({
  title: z
    .string()
    .min(2, { message: 'Title must be at least 2 characters' })
    .max(100, { message: 'Title must be at most 100 characters' }),
  description: z
    .string()
    .max(500, { message: 'Description must be at most 500 characters' })
    .optional(),
  file: z
    .instanceof(File)
    .refine((file) => file.size <= 5 * 1024 * 1024, 'File size must be less than 5MB')
    .refine(
      (file) => ['image/jpeg', 'image/png', 'application/pdf'].includes(file.type),
      'File must be an image (JPEG or PNG) or a PDF'
    ),
})

