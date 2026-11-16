import fs from 'node:fs/promises';
const list=await (await fetch('http://127.0.0.1:9230/json')).json();
const page=list.find(x=>x.type==='page');
if(!page)throw new Error('Companion WebView not found');
const ws=new WebSocket(page.webSocketDebuggerUrl);await new Promise((r,j)=>{ws.onopen=r;ws.onerror=j});let sequence=0;const pending=new Map();
ws.onmessage=e=>{const m=JSON.parse(e.data);if(m.id&&pending.has(m.id)){const {resolve,reject,timer}=pending.get(m.id);clearTimeout(timer);pending.delete(m.id);m.error?reject(new Error(JSON.stringify(m.error))):resolve(m.result);}};
function send(method,params={}){return new Promise((resolve,reject)=>{const id=++sequence;const timer=setTimeout(()=>{reject(new Error('Timed out: '+method));ws.close();},15000);pending.set(id,{resolve,reject,timer});ws.send(JSON.stringify({id,method,params}));});}
async function evaluate(expression){const r=await send('Runtime.evaluate',{expression,returnByValue:true,awaitPromise:true});if(r.exceptionDetails)throw new Error(JSON.stringify(r.exceptionDetails));return r.result.value;}
if(process.argv[2]==='navigate'){await send('Page.enable');await send('Network.setUserAgentOverride',{userAgent:process.argv[4]||'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'});await send('Page.navigate',{url:process.argv[3]});console.log('Navigated');}
else if(process.argv[2]==='screenshot'){const r=await send('Page.captureScreenshot',{format:'png'});await fs.writeFile(process.argv[3],Buffer.from(r.data,'base64'));console.log('Saved screenshot');}
else {const expression=process.argv[2]==='file'?await fs.readFile(process.argv[3],'utf8'):process.argv[2];console.log(JSON.stringify(await evaluate(expression||'({title:document.title,text:document.body.innerText,viewport:[innerWidth,innerHeight],state:state.session,sections:Object.fromEntries(Object.entries(state.sections).map(([k,v])=>[k,{state:v.state,error:v.error}]))})'),null,2));}
ws.close();
