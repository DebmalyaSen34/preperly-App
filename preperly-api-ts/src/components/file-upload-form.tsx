'use client'

import * as React from 'react'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { cn } from '@/lib/utils'
import { fileUploadAction } from '@/lib/actions'
import { Check, Upload } from 'lucide-react'

export function FileUploadForm({ className }: React.ComponentProps<typeof Card>) {
    const [state, formAction, pending] = React.useActionState(fileUploadAction, {
        defaultValues: {
            title: '',
            description: '',
            file: null,
        },
        success: false,
        errors: null,
    })

    const [fileName, setFileName] = React.useState<string | null>(null)

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setFileName(e.target.files[0].name)
        } else {
            setFileName(null)
        }
    }

    return (
        <Card className={cn('w-full max-w-md', className)}>
            <CardHeader>
                <CardTitle>Upload Your File</CardTitle>
            </CardHeader>
            <form action={formAction}>
                <CardContent className="flex flex-col gap-6">
                    {state.success ? (
                        <p className="text-muted-foreground flex items-center gap-2 text-sm">
                            <Check className="size-4" />
                            Your file has been uploaded successfully.
                        </p>
                    ) : null}
                    <div className="group/field grid gap-2" data-invalid={!!state.errors?.file}>
                        <Label htmlFor="file" className="group-data-[invalid=true]/field:text-destructive">
                            File <span aria-hidden="true">*</span>
                        </Label>
                        <div className="flex items-center gap-2">
                            <Input
                                id="file"
                                name="file"
                                type="file"
                                className="hidden"
                                onChange={handleFileChange}
                                disabled={pending}
                                aria-invalid={!!state.errors?.file}
                                aria-errormessage="error-file"
                            />
                            <Button
                                type="button"
                                variant="outline"
                                className="w-full"
                                onClick={() => document.getElementById('file')?.click()}
                                disabled={pending}
                            >
                                <Upload className="mr-2 h-4 w-4" />
                                {fileName ? 'Change File' : 'Select File'}
                            </Button>
                        </div>
                        {fileName && <p className="text-sm text-muted-foreground">{fileName}</p>}
                        {state.errors?.file && (
                            <p id="error-file" className="text-destructive text-sm">
                                {state.errors.file}
                            </p>
                        )}
                    </div>
                    <div className="group/field grid gap-2" data-invalid={!!state.errors?.title}>
                        <Label htmlFor="title" className="group-data-[invalid=true]/field:text-destructive">
                            Title <span aria-hidden="true">*</span>
                        </Label>
                        <Input
                            id="title"
                            name="title"
                            placeholder="Enter file title"
                            className="group-data-[invalid=true]/field:border-destructive focus-visible:group-data-[invalid=true]/field:ring-destructive"
                            disabled={pending}
                            aria-invalid={!!state.errors?.title}
                            aria-errormessage="error-title"
                            defaultValue={state.defaultValues.title}
                        />
                        {state.errors?.title && (
                            <p id="error-title" className="text-destructive text-sm">
                                {state.errors.title}
                            </p>
                        )}
                    </div>
                    <div className="group/field grid gap-2" data-invalid={!!state.errors?.description}>
                        <Label htmlFor="description" className="group-data-[invalid=true]/field:text-destructive">
                            Description
                        </Label>
                        <Textarea
                            id="description"
                            name="description"
                            placeholder="Enter file description"
                            className="group-data-[invalid=true]/field:border-destructive focus-visible:group-data-[invalid=true]/field:ring-destructive"
                            disabled={pending}
                            aria-invalid={!!state.errors?.description}
                            aria-errormessage="error-description"
                            defaultValue={state.defaultValues.description}
                        />
                        {state.errors?.description && (
                            <p id="error-description" className="text-destructive text-sm">
                                {state.errors.description}
                            </p>
                        )}
                    </div>
                </CardContent>
                <CardFooter>
                    <Button type="submit" disabled={pending}>
                        {pending ? 'Uploading...' : 'Upload File'}
                    </Button>
                </CardFooter>
            </form>
        </Card>
    )
}

