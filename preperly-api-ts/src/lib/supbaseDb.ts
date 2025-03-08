import { createClient } from '@supabase/supabase-js';


const supabaseUrl = process.env.SUPABASE_URL as string;
const supabaseAnonKey = process.env.SUPABASE_ANON_KEY as string;

console.log('supabaseUrl:', supabaseUrl);
console.log('supabaseAnonKey:', supabaseAnonKey);

if (!supabaseUrl || !supabaseAnonKey) {
    console.error('Supabase URL or Supabase Anon Key is missing!');
    if (process.env.NODE_ENV === 'development') {
        throw new Error('Supabase URL or Supabase Anon Key is missing!');
    }
} else {
    console.log('Supabase credentials are properly configured.');
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey);