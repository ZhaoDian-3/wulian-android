import { env } from 'cloudflare:workers';
import { handle } from '../../../lib/service.mjs';
export const dynamic='force-dynamic';
const dispatch=(request:Request)=>handle(request,env.DB);
export { dispatch as GET, dispatch as POST, dispatch as PUT, dispatch as DELETE };
