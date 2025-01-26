'use server'

import { contactFormSchema, fileUploadSchema } from './schema'
import { z } from 'zod'

export async function contactFormAction(
    _prevState: unknown,
    formData: FormData
) {
    const defaultValues = z
        .record(z.string(), z.string())
        .parse(Object.fromEntries(formData.entries()))

    try {
        const data = contactFormSchema.parse(Object.fromEntries(formData))

        // This simulates a slow response like a form submission.
        // Replace this with your actual form submission logic.
        await new Promise(resolve => setTimeout(resolve, 1000))

        console.log(data)

        return {
            defaultValues: {
                name: '',
                email: '',
                message: '',
            },
            success: true,
            errors: null,
        }
    } catch (error) {
        if (error instanceof z.ZodError) {
            return {
                defaultValues,
                success: false,
                errors: Object.fromEntries(
                    Object.entries(error.flatten().fieldErrors).map(([key, value]) => [
                        key,
                        value?.join(', '),
                    ])
                ),
            }
        }

        return {
            defaultValues,
            success: false,
            errors: null,
        }
    }
}

export async function fileUploadAction(
    _prevState: unknown,
    formData: FormData
) {
    const defaultValues = {
        title: formData.get('title') as string,
        description: formData.get('description') as string,
        file: null,
    }

    try {
        const file = formData.get('file') as File
        const data = fileUploadSchema.parse({
            title: formData.get('title'),
            description: formData.get('description'),
            file: file,
        })

        const uploadFormData = new FormData();
        uploadFormData.append('file', file);
        uploadFormData.append('title', data.title);
        if (data.description) {
            uploadFormData.append('description', data.description);
        }
        const response = await fetch('/api/auth/uploadDocuments', {
            method: 'POST',
            body: uploadFormData,
        });

        const result = await response.json();

        if(!result.ok){
            throw new Error(result.message || 'File upload failed!');
        }

        console.log('File uploaded:', data);

        return {
            defaultValues: {
                title: '',
                description: '',
                file: null,
            },
            success: true,
            errors: null,
        }
    } catch (error) {

        console.error('Error uploading file:', error);

        return {
            success: false,
            defaultValues,
            errors: (error instanceof Error) ? error.message : 'Unknown error',
        }
    }
}