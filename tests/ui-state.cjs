const fs=require('node:fs'),vm=require('node:vm'),assert=require('node:assert/strict');
const elements=new Map();const element=()=>({innerHTML:'',textContent:'',hidden:false,style:{},dataset:{},scrollTop:0,open:false,showModal(){this.open=true},close(){this.open=false},getClientRects(){return [1]}});
const document={documentElement:{style:{setProperty(){}}},querySelector(s){if(!elements.has(s))elements.set(s,element());return elements.get(s)},querySelectorAll(){return []}};
const ctx=vm.createContext({document,window:{},console,setInterval(){},clearTimeout(){},setTimeout(){},URL,Date,Number,String,JSON,Map,Set});
vm.runInContext(fs.readFileSync('assets/catalog.js','utf8'),ctx);vm.runInContext(fs.readFileSync('assets/ui.js','utf8'),ctx);vm.runInContext(fs.readFileSync('assets/app.js','utf8'),ctx);vm.runInContext(fs.readFileSync('assets/game-ui.js','utf8'),ctx);
vm.runInContext(fs.readFileSync('assets/official-map.js','utf8'),ctx);vm.runInContext(fs.readFileSync('assets/daily.js','utf8'),ctx);vm.runInContext(fs.readFileSync('assets/terminal.js','utf8'),ctx);
const run=s=>vm.runInContext(s,ctx);let checks=0;function check(s,msg){assert.ok(run(s),msg);checks++;}
const profile=JSON.parse(fs.readFileSync('tests/fixtures/profile.json','utf8'));
ctx.profile=profile;run(`state={uid:'1000000001',server:'prod_gf_jp',session:'disconnected',sections:{showcase:{state:'ready',at:Date.now(),data:profile}}}`);
check(`renderAgents().includes('Alice')`,'Actual showcase names render');
check(`(render(),$('#content').innerHTML.includes('eridu-wait'))`,'Cold launch remains empty before game identity verification');
check(`!$('#content').innerHTML.includes('Battery Charge')`,'No invented battery information before authentication');
check(`normalized(publicAgents()[0]).discs.length===${profile.PlayerInfo.ShowcaseDetail.AvatarList[0].EquippedList.length}`,'Showcase disc list preserved');
check(`weaponName(normalized(publicAgents()[0]).weapon)==='Practiced Perfection'`,'W-Engine localization');
check(`propertyValue({PropertyId:20103,PropertyValue:240,PropertyLevel:3})==='7.2%'`,'Enka substat roll scaling');
check(`propertyValue({PropertyId:11103,PropertyValue:550},{main:true,level:15,rarity:4})==='2,200'`,'Disc level and rarity scaling');
check(`!imageUrl('https://evil.example/x.png')`,'External artwork host rejected');
check(`esc('<img onerror=alert(1)>').includes('&lt;img')`,'Text is escaped');
check(`pullRarity({rarity:'S'})==='S'&&pullRarity({rarity:'A'})==='A'`,'HoYoLAB rank letters handled');
check(`pullDate({date:{year:2026,month:11,day:15,hour:2,minute:3,second:4}})==='2026-11-15 02:03:04'`,'HoYoLAB structured pull dates');
const notes={energy:{progress:{current:120,max:240},restore:21600},vitality:{current:200,max:400},card_sign:'CardSignDone',vhs_sale:{sale_state:'SaleStateDoing'},weekly_task:{cur_point:700,max_point:1000,refresh_time:86400},bounty_commission:{num:2,total:3},survey_points:{num:300,total:400},temple_running:{expedition_state:'ExpeditionStateEnd',bench_state:'BenchStateCanProduce',shelve_state:'ShelveStateSoldOut'}};
ctx.notes=notes;run(`state.session='connected';state.sections.notes={state:'ready',at:Date.now(),data:notes};`);
check(`dailyTiles().includes('120')&&dailyTiles().includes('Collect')&&dailyTiles().includes('700 / 1000')`,'Daily notes normalize actual upstream field names');
check(`duration(21600)==='6h 0m'&&dailyTiles().includes('Battery Charge')`,'Battery countdown derives from recovery time');
run(`state.sections.notes.state='error';state.sections.notes.error='Network unavailable';`);
check(`sectionMessage('notes').includes('Network unavailable')`,'Stale daily data carries error');
run(`local.teams=[{name:'<test>',agents:['1401','1411','1331'],rotation:'Yuzuha → Vivian → Alice',notes:'Counter the boss',mode:'Shiyu',active:true}];`);
check(`renderTeams().includes('&lt;test&gt;')&&renderTeams().includes('Yuzuha → Vivian → Alice')`,'Saved team text escapes HTML and keeps rotations');
run(`state.sections.earnings={state:'ready',at:Date.now(),data:{data_month:'202611',optional_month:['202611'],month_data:{list:[{count:1000,data_name:'Polychromes',data_type:'PolychromesData'}],income_components:[{action:'event_rewards',num:1000,percent:100}]}}};`);
check(`renderEarnings().includes('1,000')&&renderEarnings().includes('Events')`,'Monthly income and chart render');
run(`state.sections.shiyu={state:'ready',at:Date.now(),data:{hadal_info_v2:{total_score:12345,avatar_list:[{id:1401}]}}};`);
check(`renderChallenges().includes('12,345')&&renderChallenges().includes('Alice')`,'Nested challenge results and teams render');
run(`state.sections.events={state:'ready',at:Date.now(),data:{activity_list:[{name:'Event <test>',start_ts:1,end_ts:9999999999,monochrome_cnt:100,monochrome_got_cnt:50}]}};`);
check(`renderCalendar().includes('Event &lt;test&gt;')&&renderCalendar().includes('50 Remaining')`,'Calendar rewards and escaped names');
run(`state.sections.threshold={state:'ready',at:Date.now(),data:{void_front_battle_detail:{void_front_battle_abstract_info_brief:{total_score:20000,rank_percent:123}}}};challengeMode='threshold';`);
check(`renderChallenges().includes('20,000')&&renderChallenges().includes('1.23%')`,'Threshold brief alias and rank-percent scaling');
for(const t of ['Today','Agents','Guide','Challenges','Calendar','Earnings','Signals']){run(`tab='${t}';render()`);checks++;}
run(`modalAgent='1401';agentPage='Build';renderAgentModal();`);checks++;
run(`agentPage='Skills';renderAgentModal();agentPage='Upgrade';renderAgentModal();agentPage='History';renderAgentModal();`);checks+=3;
check(`art(publicAgents()[0]).includes('cutouts/')`,'Roster uses transparent upper-body cutouts');
check(`renderBuild(normalized(publicAgents()[0])).includes('loadout-wheel')`,'Equipped loadout uses circular slot selector');
check(`renderSignals().includes('currency-art') || !data('gacha')`,'Currency artwork in Signal Search');
check(`skillAdvice({first:[0,1],second:[3],third:[2]}).includes('Special Attack')&&!skillAdvice({first:[0]}).includes('First priority: 0')`,'Upgrade priorities use named skills');
check(`lostVoidProgress({abyss_level:{cur_level:5,max_level:10},abyss_collect:[{type:3,cur_collect:20,max_collect:30}]}).includes('Resonium Research')`,'Lost Void collections use in-game names');
check(`renderBattleTeams({list:[{boss:[{name:'Test Boss'}],score:10000,avatar_list:[{id:1401,level:60}]}]}).includes('Test Boss')`,'Battle results show boss and close-up team');
console.log(`${checks} UI/data checks passed`);


check(`(receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true,agents:[{id:'1401'},{id:'1411'},{id:'1331'}],activeAgent:'1401'}}),sessionReady&&gameStatus()==='In Combat'&&renderLive().includes('Alice'))`,'Matching game session opens live squad');
check(`!renderLive().includes('Yanagi')`,'Live squad does not substitute other owned agents');
check(`(receiveGame({phase:'combat',context:{uid:'1000000000',uidConfirmed:true}}),!sessionReady)`,'A different game UID keeps the account hidden');
check(`(receiveGame({phase:'not_visible'}),!sessionReady&&gameStatus()==='Waiting For Game')`,'Hidden game resets the live session');
check(`(receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true,enemyCount:2}}),!renderLive().includes('2 Visible Hostiles')&&renderLive().includes('Target Not Identified'))`,'Screen health bars cannot claim an enemy count');
check(`(receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true,enemy:{name:'Haytor'}}}),renderLive().includes('Haytor'))`,'Recognized enemy names appear in combat');
check(`(receiveGame({phase:'login'}),!sessionReady&&gameStatus()==='Waiting For Login')`,'Logging out empties the dashboard');
check(`!TABS.includes('Teams')&&TABS.includes('Guide')`,'Guide replaces manual teams');
check(`!renderLive().includes('checks/sec')&&!$('#press-strip').innerHTML.includes('Refresh')`,'Technical polling labels and control instructions removed');
console.log('9 terminal-state checks passed');
run(`receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true},telemetry:{at:Date.now(),fps:{available:true,value:59.8},playtime:{available:true,totalMs:8*3600000,source:'Android Usage History'}}});`);
check(`$('#game-fps b').textContent===60`,'Game FPS is rounded from measured presented frames');
check(`$('#thor-hours b').textContent==='8.0'`,'Thor hours convert recorded milliseconds');
run(`receiveGame({phase:'not_visible',telemetry:{at:Date.now(),fps:{available:true,value:60},playtime:{available:true,totalMs:8*3600000}}});`);
check(`$('#game-fps b').textContent==='—'&&$('#thor-hours b').textContent==='8.0'`,'Hidden game clears FPS and retains historical hours');
run(`receiveGame({phase:'combat',telemetry:{at:Date.now()-6000,fps:{available:true,value:60}}});`);
check(`$('#game-fps b').textContent==='—'`,'Stale telemetry cannot display live FPS');
console.log('4 telemetry UI checks passed');

check(`(receiveGame({phase:'exploring',context:{uid:state.uid,uidConfirmed:true}}),receiveGame({phase:'exploring',context:{uid:'1000000000',uidConfirmed:false}}),sessionReady)`,'An unconfirmed OCR error cannot clear a verified session');
check(`(receiveGame({phase:'unavailable'}),sessionReady)`,'Short capture failures preserve the verified account');
check(`(receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true}}),phaseSince=1000,receiveGame({phase:'unknown'}),receiveGame({phase:'combat'}),phaseSince===1000)`,'HUD occlusion cannot restart the encounter timer');
check(`(receiveGame({phase:'results'}),encounterEndedAt>0)`,'Results stop the encounter clock');
check(`thorHours(1234567*3600000)==='1.2M'`,'Large playtime values use compact notation');
console.log('5 session and duration regressions passed');

check(`(receiveGame({phase:'combat',context:{uid:state.uid,uidConfirmed:true,agents:[{id:'1211'},{id:'1611'},{id:'1101'}],activeAgent:'1101'}}),liveAgents().length===3&&liveAgents()[1].id==='1101')`,'On-field character occupies the center');
check(`(receiveGame({phase:'combat',context:{agents:[{id:'1211'},{id:'1611'},{id:'1101'}],activeAgent:'1211'}}),liveAgents()[1].id==='1211')`,'Switching changes the centered agent');
check(`(receiveGame({phase:'combat',context:{agents:[]}}),liveAgents().length===3&&liveAgents().every(x=>!x.id))`,'Unknown initial squad reserves three honest slots');
console.log('3 encounter arrangement checks passed');

check(`(receiveGame({phase:'exploring',at:Date.now(),context:{uid:state.uid,uidConfirmed:true},dailyHistory:{lastTriggeredAt:Date.now()-1000,errandsAvailable:false,nextResetAt:Date.now()+3600000}}),dailyHistoryCard().includes('Daily Errands Reset In')&&dailyHistoryCard().includes('Battery Farming Available Anytime'))`,'Daily reset countdown does not lock Battery farming');
check(`dailyMini({running:true,startedAt:Date.now()-60000,stageIndex:2,stageTotal:6,estimatedDurationMs:180000}).includes('2/6')&&dailyMini({}).includes('0m 0s Elapsed')`,'Progress uses verified tasks and tolerates missing timestamps');
check(`dailyMini({running:true,startedAt:Date.now()-240000,estimatedDurationMs:180000}).includes('Estimate Updating')`,'Slow loading cannot produce a negative ETA');
check(`dailyClock(25*3600000)==='25h 0m'`,'Long elapsed runs do not wrap their hours');
console.log('4 daily progress/reset UI checks passed');

run(`let dailyToastCount=0;const previousDailyToast=toast;toast=()=>dailyToastCount++;gameObservation.automation={id:'notice-test',state:'attention',message:'Needs Attention',running:false};dailyRunPaint='';updateDailyRun();dailyRunPaint='';updateDailyRun();`);
check(`dailyToastCount===1`,'Routine status clock repaint cannot repeat a stopped/error toast');
run(`toast=previousDailyToast;`);
console.log('1 daily notification regression passed');

run(`gameObservation.automation={};gameObservation.dailyHistory={errandsAvailable:false};showDailyPlan();`);
check(`$('#modal-body').innerHTML.includes('Farm Only Until Reset')&&$('#modal-body').innerHTML.includes('route-skipped')&&$('#modal-body').innerHTML.includes('Daily Rewards · Always Included')`,'Completed daily cycle exposes farming without offering daily errands again');
run(`gameObservation.dailyHistory={errandsAvailable:true};dailyPlan.farmOnly=false;local.options={...local.options,dailyPlan:{...dailyPlan,farmOnly:false}};showDailyPlan();`);
check(`$('#modal-body').innerHTML.includes('daily-checks')&&$('#modal-body').innerHTML.includes('Full Routine')`,'New reset restores full daily-route controls');
console.log('2 daily plan eligibility regressions passed');

run(`gameObservation.dailyHistory={errandsAvailable:true,verifiedTasks:{coffee:true,draw:true}};showDailyPlan(0);`);
check(`$('#modal-body').innerHTML.includes('Done')&&!dailyForecast().includes('+80 From Coffee')`,'Verified tasks show Done and do not count coffee Battery twice');
run(`gameObservation.dailyHistory={errandsAvailable:true,verifiedTasks:{}};showDailyPlan();`);
check(`!$('#modal-body').innerHTML.includes('Done')&&dailyForecast().includes('+80 From Coffee')`,'Reset clears completed badges and restores the coffee forecast');
console.log('2 verified-task UI regressions passed');

run(`gameObservation.dailyHistory={verifiedTasks:{temple:true}};`);
check(`templeDailyState().complete&&templeDailyState().label==='Done'`,'Verified Temple management is green even when HoYo notes lag');
run(`gameObservation.dailyHistory={};`);
check(`storeDailyState().label==='Running'&&storeDailyState().complete`,'Video Store status describes active business');
check(`templeDailyState().label==='Collect'&&templeDailyState().attention`,'Returned Bangboo clearly needs collection');
check(`eriduDailyGrid().indexOf('Bangboo')<eriduDailyGrid().indexOf('Hollow Zero')&&eriduDailyGrid().indexOf('Weekly Tasks')>eriduDailyGrid().indexOf('Hollow Zero')`,'Bangboo occupies the former Weekly Tasks position');
run(`dailyPlan.target='discs';dailyPlan.stage='';dailyPlan.battery=true;saveDailyPlan();showDailyPlan(1);`);
check(`$('#modal-body').innerHTML.includes('Choose Stage')&&$('#modal-body').innerHTML.includes('60 / Run')`,'Disc plan exposes its stage and exact final-run top-up rule');
console.log('5 Thor daily enhancement checks passed');

run(`gameObservation.automation={running:true,at:Date.now()};const beforeRunModal=$('#modal-body').innerHTML;showDailyPlan();`);
check(`$('#modal-body').innerHTML===beforeRunModal&&!dailyMini({}).includes('onclick="showDailyPlan()"')`,'Running progress never opens a fullscreen modal');
run(`gameObservation.automation={};explainStageRefresh();`);
check(`$('#modal-body').innerHTML.includes('Does Not Start Battles Or Spend Battery')`,'Stage list update explains its actual controller navigation');
run(`gameObservation.dailyHistory={};const savedSale=notes.vhs_sale.sale_state,savedTemple=notes.temple_running;notes.vhs_sale.sale_state='SaleStateNotStarted';notes.temple_running={expedition_state:'ExpeditionStateNone'};`);
check(`storeDailyState().label==='Closed'&&templeDailyState().label==='Idle'`,'Inactive services use short Closed and Idle labels');
run(`notes.vhs_sale.sale_state=savedSale;notes.temple_running=savedTemple;`);
check(`dailyDiscSets('swiftspine and splitclaw').length===2&&dailyDiscArt('swiftspine and splitclaw').includes('SuitFeatheredFate')&&dailyDiscSets('sharp fang and biunt axe').length===2&&dailyDiscSets('Unknown Stage').length===0`,'Disc artwork uses known set pairs, tolerates the scanned Blunt spelling and does not invent unknown rewards');
console.log(`${checks} total UI checks passed`);
