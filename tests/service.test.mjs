import assert from 'node:assert/strict';import {DatabaseSync} from 'node:sqlite';import fs from 'node:fs';import {handle,authorize,hash,random} from '../lib/service.mjs';
const sqlite=new DatabaseSync(':memory:');sqlite.exec(fs.readFileSync('drizzle/0000_happy_ben_grimm.sql','utf8'));
const db={
 prepare(sql){return {bind(...args){return {
  async first(){return sqlite.prepare(sql).get(...args)||null},
  async all(){return {results:sqlite.prepare(sql).all(...args)}},
  async run(){return sqlite.prepare(sql).run(...args)}
 }}}},
 async batch(a){return Promise.all(a.map(v=>v.run()))}
};
const url='https://five.test';const request=(path,method='GET',data=null,token='')=>new Request(url+path,{method,headers:{'content-type':'application/json',...(token?{authorization:'Bearer '+token}:{})},body:data?JSON.stringify(data):undefined});
async function call(p,m,d,t,status=200){const r=await handle(request(p,m,d,t),db);let b=await r.json();assert.equal(r.status,status,JSON.stringify(b));return b}
const plan=JSON.parse(fs.readFileSync('android/assets/default_plan.json','utf8'));const record={date:'2026-11-05',day:plan.days[0],note:'private',finished:false,rows:plan.days[0].items.map(e=>[{weight:20,reps:12,done:true}])};
await call('/api/entries','GET',null,'',401);
const pair=await call('/api/device/start','POST',{});assert.equal(pair.secret.length,64);
await call('/api/device/poll','POST',{code:pair.code,secret:random()},'',410);
assert.equal((await call('/api/device/poll','POST',{code:pair.code,secret:pair.secret})).pending,true);
const row=sqlite.prepare('SELECT * FROM devices WHERE code=?').get(pair.code);
let form=new URLSearchParams({code:pair.code,csrf:row.csrf});let req=new Request(url+'/authorize',{method:'POST',headers:{origin:'https://evil.test'},body:form});assert.equal((await authorize(req,db,{userId:'alice',displayName:'Alice'})).status,403);
req=new Request(url+'/authorize',{method:'POST',headers:{origin:url},body:form});assert.equal((await authorize(req,db,{userId:'alice',displayName:'Alice'})).status,303);
let login=await call('/api/device/poll','POST',{code:pair.code,secret:pair.secret});const alice=login.token;assert.equal(login.userId,'alice');assert.equal((await call('/api/device/poll','POST',{code:pair.code,secret:pair.secret})).token,alice);
const bob=random();sqlite.prepare('INSERT INTO tokens VALUES (?,?,?,?)').run(await hash(bob),'bob','Bob',Date.now()+1000000);
let first=await call('/api/entries','PUT',{id:'record_1',kind:'record',body:record,baseVersion:0},alice);assert.equal(first.version,1);assert.equal((await call('/api/entries','GET',null,bob)).entries.length,0);assert.equal((await call('/api/entries','GET',null,alice)).entries[0].body.note,'private');
let conflict=await call('/api/entries','PUT',{id:'record_1',kind:'record',body:{...record,note:'stale'},baseVersion:0},alice,409);assert.equal(conflict.conflict.body.note,'private');
assert.equal((await call('/api/entries','PUT',{id:'record_1',kind:'record',body:{...record,note:'updated'},baseVersion:1},alice)).version,2);
await call('/api/entries','PUT',{id:'record_1',kind:'record',body:record,baseVersion:1},bob,409);
let share=await call('/api/shares','POST',{plan:{...plan,privateLog:'NEVER_SHARE'}},alice);assert.equal(share.plan.privateLog,undefined);assert.equal((await call('/api/shares/'+share.id,'GET',null,bob)).plan.days.length,5);
await call('/api/shares/'+share.id,'DELETE',null,bob);await call('/api/shares/'+share.id,'GET',null,bob);await call('/api/shares/'+share.id,'DELETE',null,alice);await call('/api/shares/'+share.id,'GET',null,bob,404);
await call('/api/entries','PUT',{id:'bad',kind:'record',body:{...record,rows:[]},baseVersion:0},alice,400);
await call('/api/logout','POST',{},alice);await call('/api/me','GET',null,alice,401);await call('/api/device/poll','POST',{code:pair.code,secret:pair.secret},'',410);
console.log('PASS: pairing, CSRF, token retries, per-user isolation, optimistic locking, plan-only sharing, owner-only revocation, validation, logout.');
