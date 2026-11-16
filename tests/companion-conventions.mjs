import fs from 'node:fs/promises';import assert from 'node:assert/strict';
const pages=await(await fetch('http://127.0.0.1:9230/json/list')).json(),page=pages.find(x=>x.type==='page'),ws=new WebSocket(page.webSocketDebuggerUrl);await new Promise((r,j)=>{ws.onopen=r;ws.onerror=j});let id=0;const jobs=new Map();ws.onmessage=e=>{const m=JSON.parse(e.data);if(m.id&&jobs.has(m.id)){const j=jobs.get(m.id);jobs.delete(m.id);m.error?j.reject(Error(JSON.stringify(m.error))):j.resolve(m.result);}};
function call(method,params={}){return new Promise((resolve,reject)=>{const n=++id;jobs.set(n,{resolve,reject});ws.send(JSON.stringify({id:n,method,params}));});}
async function run(expression){const r=await call('Runtime.evaluate',{expression,returnByValue:true,awaitPromise:true});if(r.exceptionDetails)throw Error(JSON.stringify(r.exceptionDetails));return r.result.value;}
await call('Emulation.setDeviceMetricsOverride',{width:538,height:468,deviceScaleFactor:1,mobile:false});
let checks=0;async function check(expression,message){assert(await run(expression),message);checks++;}
await run(`closeModal();receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true,agents:[{id:'1401'},{id:'1411'},{id:'1331'}],activeAgent:'1401'}});setTab('Today');receiveController({available:true,at:Date.now(),buttons:{A:true,B:true,L2:true},pulses:{X:Date.now()},axes:{lx:.8,ly:-.5}});`);
await check(`document.querySelectorAll('#press-strip [data-input]').length===16`,'All sixteen gamepad controls');
await check(`['A','B','X','L2'].every(k=>document.querySelector('[data-input="'+k+'"]').classList.contains('pressed'))`,'Held and briefly tapped keys light up');
await check(`getComputedStyle(document.querySelector('[data-input=A]')).backgroundColor==='rgba(0, 0, 0, 0)'`,'Feedback has no keycap box');
await check(`document.querySelector('[data-stick=l]').classList.contains('down')&&document.querySelector('[data-stick=l]').style.getPropertyValue('--x')==='4px'`,'Analog sticks show the actual axis movement');
await check(`(()=>{const bar=document.querySelector('#press-strip').getBoundingClientRect(),first=document.querySelector('.input-key').getBoundingClientRect(),last=[...document.querySelectorAll('.analog-stick')].at(-1).getBoundingClientRect();return first.left<bar.left+5&&last.right>bar.right-5})()`,'Controls use the full width');
await run(`receiveController({available:true,at:Date.now()-3000,buttons:{A:true},axes:{lx:1}});`);
await check(`!document.querySelector('#press-strip .pressed')&&!document.querySelector('#press-strip .analog-stick.down')`,'Stale input never stays lit');
await run(`showCompanionOptions()`);await check(`document.querySelector('.developer-credit strong').textContent==='gbppimentel-dev'`,'Exact compact creator credit');
await check(`document.querySelector('.github-mark').naturalWidth>0`,'Bundled GitHub artwork loads');
await run(`setControllerFeedback(false)`);await check(`getComputedStyle(document.querySelector('#press-strip')).display==='none'`,'Hidden input preference applies');
await run(`restoreLocal(JSON.stringify(local))`);await check(`document.querySelector('footer').classList.contains('feedback-hidden')`,'Input preference survives restored saved options');
await run(`setControllerFeedback(true);showCredits()`);await check(`document.querySelector('#modal-body').innerText.includes('HoYoverse')&&document.querySelector('#modal-body').innerText.includes('MIT')`,'Game rights and adapted-code notices are separate');
await run(`closeModal();agentKind='Agents';library=false;setTab('Agents');state.sections.roster={state:'cached',at:Date.now()-3600000,data:{avatar_list:[]}};state.sections.notes={state:'ready',at:Date.now(),data:{}};`);
await check(`visibleFreshness().startsWith('Saved ')&&visibleFreshness().includes('1 Hr Ago')`,'Fresh daily notes cannot conceal a stale roster');
await run(`state.sections.notes={state:'ready',at:Date.now(),data:{energy:{progress:{current:240,max:240}},vitality:{current:400,max:400}}};`);
await check(`proxyNextSteps()[0].title==='Spend Your Battery Charge'&&!proxyNextSteps().some(x=>x.title==='Finish Daily Activity')`,'Next steps use actual incomplete progress');
await check(`recommendedWeapons({main:{id:0},backup:{id:0}})===''`,'Placeholder recommendations are not invented gear');
await run(`window.savedReviewAccount=state.sections.account;delete state.sections.account;`);
await check(`!renderCompletion().includes('All Supplied Areas Complete')`,'Missing progress cannot become completion');
await run(`window.savedReviewSession=state.session;state.session='offline';`);
await check(`locked('unloaded-test','Build Guide').includes('Not Saved On This Thor')`,'Offline missing records do not claim an active account sync');
await run(`state.sections.account=savedReviewAccount;state.session=savedReviewSession;`);
await run(`showCompanionOptions()`);await new Promise(r=>setTimeout(r,80));const image=await call('Page.captureScreenshot',{format:'png'});await fs.writeFile('review/hades-conventions/options.png',Buffer.from(image.data,'base64'));await run(`showCredits()`);const credits=await call('Page.captureScreenshot',{format:'png'});await fs.writeFile('review/hades-conventions/credits.png',Buffer.from(credits.data,'base64'));console.log(checks+' companion-convention checks passed');ws.close();
