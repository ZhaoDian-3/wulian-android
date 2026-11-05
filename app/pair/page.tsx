import { env } from 'cloudflare:workers';
import { requireChatGPTUser,chatGPTSignOutPath } from '../chatgpt-auth';
export const dynamic='force-dynamic';
export default async function Pair({searchParams}:{searchParams:Promise<{code?:string,done?:string}>}){
 const {code='',done}=await searchParams;if(!/^[a-f0-9]{64}$/.test(code))return <main><h1>请从五练 App 发起登录</h1></main>;
 const user=await requireChatGPTUser('/pair?code='+code);
 const d=await env.DB!.prepare('SELECT phrase,csrf,user_id,expires FROM devices WHERE code=?').bind(code).first<{phrase:string,csrf:string,user_id:string|null,expires:number}>();
 if(!d||d.expires<Date.now())return <main><h1>登录请求已过期</h1><p>请返回五练 App，重新点击登录。</p></main>;
 if(d.user_id)return <main><div className="logo">五练 /</div><h1>{d.user_id===user.userId?'手机授权成功':'该请求已被使用'}</h1><p>请返回五练 App，点击「已授权，完成登录」。</p><p>训练数据将按账号独立保存。</p></main>;
 return <main><div className="logo">五练 /</div><span className="eyebrow">连接你的训练记录</span><h1>授权这台安卓手机</h1><p>当前账号：{user.displayName}</p><p>请核对 App 显示的验证码：</p><div className="code">{d.phrase}</div><p>仅当你刚刚在自己的五练 App 发起登录，且验证码一致时授权。授权后，这台手机可读取和修改你的五练记录。</p><form method="post" action="/authorize"><input type="hidden" name="code" value={code}/><input type="hidden" name="csrf" value={d.csrf}/><button>验证码一致，授权手机</button></form><a href={chatGPTSignOutPath('/pair?code='+code)} target="_top">切换账号</a><p className="small">不分享训练日志。计划仅在你主动生成分享码后可被接收者导入。</p></main>
}
