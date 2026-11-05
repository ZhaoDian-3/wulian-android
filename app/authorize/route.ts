import { env } from 'cloudflare:workers';
import { getChatGPTUser } from '../chatgpt-auth';
import { authorize } from '../../lib/service.mjs';
export const dynamic='force-dynamic';
export async function POST(request:Request){return authorize(request,env.DB,await getChatGPTUser())}
