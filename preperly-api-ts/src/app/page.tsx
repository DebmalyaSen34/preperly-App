import { ContactForm } from '@/components/contact-form'
import { FileUploadForm } from '../components/file-upload-form';
export default function Page() {
  return (
    <div className="flex min-h-svh items-center justify-center p-4">
      <div className="grid gap-8 md:grid-cols-2">
        <ContactForm />
        <FileUploadForm />
      </div>
    </div>
  )
}
