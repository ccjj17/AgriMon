const express = require('express');
const { spawn } = require('child_process');
const cors = require('cors');
const fs = require('fs');       // ✅ 规范：依赖移到了最顶部
const path = require('path');   // ✅ 规范：依赖移到了最顶部

const app = express();
app.use(cors());
app.use(express.json());

// ==================== 接口 1：登录验证 API ====================
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    let responded = false;
    let javaOutput = '';

    console.log(`\n🌐 [中端] 收到前端网页请求 -> 正在启动后台 Java 引擎验证...`);

    const javaProcess = spawn('java', ['SmartFarmSystem']);
    javaProcess.stdin.write(`1\n${username}\nn\n${password}\nn\n`);
    javaProcess.stdin.end();

    javaProcess.stdout.on('data', (data) => {
        const text = data.toString();
        javaOutput += text;
        console.log(`[Java 实时流] ${text.trim()}`);

        if (javaOutput.includes('Login Successful!') && !responded) {
            responded = true;
            javaProcess.kill();
            console.log(`✅ [中端] Java 引擎验证通过！`);
            return res.json({ status: "success", message: "登录成功！", username: username });
        }
    });

    javaProcess.on('close', (code) => {
        if (!responded) {
            responded = true;
            console.log(`[Java 进程结束] 退出码: ${code}`);
            if (javaOutput.includes('Login Failed!')) {
                return res.json({ status: "error", message: "Java 数据库判定：账号或密码错误！" });
            } else {
                return res.json({ status: "error", message: "Java 引擎异常退出。请检查密码是否为 database.txt 里的真实密码！" });
            }
        }
    });

    javaProcess.stderr.on('data', (data) => {
        console.error(`🚨 [Java 报错]: ${data}`);
    });
});

// ==================== 接口 2：获取农民的农场列表 API ====================
app.get('/api/farmer/farms', (req, res) => {
    const username = req.query.username; 
    if (!username || username === 'undefined') {
        return res.status(400).json({ status: "error", message: "缺少有效用户名" });
    }

    console.log(`\n🌾 [中端] 正在为农民 [${username}] 检索农场列表...`);

    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        const lines = dbContent.split('\n');

        let userId = '';
        let farms = [];
        let isUserSection = false;
        let isFarmSection = false;

        for (let line of lines) {
            line = line.trim();
            if (line === '[USERS]') { isUserSection = true; isFarmSection = false; continue; }
            if (line === '[FARMS]') { isUserSection = false; isFarmSection = true; continue; }
            if (line.startsWith('[')) { isUserSection = false; isFarmSection = false; continue; }

            if (isUserSection && line) {
                const parts = line.split('|');
                if (parts[1] === username) {
                    userId = parts[0]; 
                    break;
                }
            }
        }

        if (!userId) {
            return res.json({ status: "success", farms: [] }); 
        }

        isFarmSection = false;
        for (let line of lines) {
            line = line.trim();
            if (line === '[FARMS]') { isFarmSection = true; continue; }
            if (line.startsWith('[') && line !== '[FARMS]') { isFarmSection = false; continue; }

            if (isFarmSection && line) {
                const parts = line.split('|'); 
                if (parts[2] === userId) {
                    farms.push({
                        farmId: parts[0],
                        farmName: parts[1],
                        ownerId: parts[2]
                    });
                }
            }
        }

        console.log(`✅ [中端] 成功找到 ${farms.length} 个农场，正发往前端。`);
        res.json({ status: "success", farms: farms });

    } catch (err) {
        console.error("🚨 读取数据库文件失败:", err);
        res.status(500).json({ status: "error", message: "服务器内部读取错误" });
    }
});

// ==================== 接口 3：获取指定农场的 3x3 地块数据（✨8大作物完整知识库版） ====================
app.get('/api/farmer/plots', (req, res) => {
    const farmId = req.query.farmId; 
    if (!farmId || farmId === 'undefined') {
        return res.status(400).json({ status: "error", message: "缺少农场 ID" });
    }

    // 🧠 补全全套 8 种作物的后端物联网大数据库，完美对应前端的选择名字
    const backendCropKnowledgeBase = {
        'Chili': {
            desc: 'Chili peppers thrive in well-drained tropical soil in Sarawak. They require steady warmth, strict moisture monitoring to prevent root rot, and reward farmers with a relatively short harvest cycle.',
            growth: '~65d', sunlight: '6h', temp: '31.2°C', wind: '3km/h', moisture: '62%', condition: 'Healthy', pest: '~3%', weed: '7%'
        },
        'Durian': {
            desc: 'Durian is the king of fruits, thriving in rich, well-drained tropical soil in Sarawak. Requires meticulous moisture control and long sunny periods to harvest premium grades.',
            growth: '~120d', sunlight: '7h', temp: '29.5°C', wind: '5km/h', moisture: '55%', condition: 'Excellent', pest: '~5%', weed: '12%'
        },
        'Corn': {
            desc: 'Corn is a highly adaptable crop that grows well in warm and sunny conditions. It requires fertile soil and steady water supply, grows quickly.',
            growth: '~80d', sunlight: '4h', temp: '28°C', wind: '4km/h', moisture: '58%', condition: 'Healthy', pest: '~6%', weed: '16%'
        },
        'Paddy': {
            desc: 'Paddy requires abundant water frameworks and continuous flooding control. It is a vital primary crop managed carefully via localized smart irrigation algorithms.',
            growth: '~105d', sunlight: '5h', temp: '27.8°C', wind: '2km/h', moisture: '85%', condition: 'Stable', pest: '~2%', weed: '19%'
        },
        'Banana': {
            desc: 'Banana plants thrive in humid tropical areas with well-drained soil. They demand significant potassium levels and high ambient moisture to ensure fruit bunch sizing.',
            growth: '~90d', sunlight: '6h', temp: '30.1°C', wind: '4km/h', moisture: '70%', condition: 'Healthy', pest: '~4%', weed: '8%'
        },
        'Cocoa': {
            desc: 'Cocoa plants flourish beneath shading canopies in rich Sarawakian loam. Strict temperature ranges and meticulous humidity management protect pods from black pod disease.',
            growth: '~150d', sunlight: '3h', temp: '26.4°C', wind: '2km/h', moisture: '75%', condition: 'Good', pest: '~8%', weed: '11%'
        },
        'Oil Palm': {
            desc: 'Oil Palm is a monumental pillar of rural development in Sarawak. Demands exceptional solar radiation, heavy year-round tropical rainfall, and structured nutrient schedules.',
            growth: '~180d', sunlight: '7h', temp: '32.0°C', wind: '5km/h', moisture: '68%', condition: 'Robust', pest: '1%', weed: '5%'
        },
        'Pineapple': {
            desc: 'Pineapple thrives brilliantly in acidic peat soils across Sarawak fields. It is remarkably drought-tolerant, requiring minimal irrigation but maximum exposure to direct sunlight.',
            growth: '~140d', sunlight: '8h', temp: '33.3°C', wind: '3km/h', moisture: '45%', condition: 'Vibrant', pest: '~2%', weed: '14%'
        }
    };

    console.log(`\n📊 [中端] 正在为农场 [${farmId}] 检索 3x3 地块并注入全套物联网数据...`);

    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        const lines = dbContent.split('\n');

        let plots = [];
        let isPlotSection = false;

        for (let line of lines) {
            line = line.trim();
            if (line === '[PLOTS]') { isPlotSection = true; continue; }
            if (line.startsWith('[') && line !== '[PLOTS]') { isPlotSection = false; continue; }

            if (isPlotSection && line) {
                const parts = line.split('|'); 
                if (parts[2] === farmId) {
                    const cropName = parts[1] || 'Corn';
                    // 从全新补全的后端知识库里捞出数据
                    const cropDetails = backendCropKnowledgeBase[cropName] || backendCropKnowledgeBase['Corn'];

                    plots.push({
                        plotId: parts[0],
                        plotName: cropName,
                        farmId: parts[2],
                        iotDetails: cropDetails // 打包发射给前端
                    });
                }
            }
        }

        plots.sort((a, b) => {
            const numA = parseInt(a.plotId.split('-')[2]);
            const numB = parseInt(b.plotId.split('-')[2]);
            return numA - numB;
        });

        res.json({ status: "success", plots: plots });

    } catch (err) {
        console.error("🚨 读取地块数据失败:", err);
        res.status(500).json({ status: "error", message: "服务器内部读取错误" });
    }
});

// ==================== 接口 4：更新指定农场所有地块的作物类型 ====================
app.post('/api/farmer/farm/updateCrop', (req, res) => {
    const { farmId, cropName } = req.body;
    if (!farmId || !cropName) {
        return res.status(400).json({ status: "error", message: "缺少农场 ID 或作物名称" });
    }

    console.log(`\n🚜 [中端] 收到农场改种请求 -> 农场 ID: [${farmId}], 全员改种: [${cropName}]`);

    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        const lines = dbContent.split('\n');
        
        let isPlotSection = false;
        
        const updatedLines = lines.map(line => {
            const trimmed = line.trim();
            if (trimmed === '[PLOTS]') { isPlotSection = true; return line; }
            if (trimmed.startsWith('[') && trimmed !== '[PLOTS]') { isPlotSection = false; return line; }

            if (isPlotSection && trimmed) {
                const parts = trimmed.split('|'); 
                if (parts[2] === farmId) {
                    parts[1] = cropName; 
                    return parts.join('|');
                }
            }
            return line;
        });

        fs.writeFileSync(dbPath, updatedLines.join('\n'), 'utf-8');
        console.log(`✅ [中端] database.txt 成功改写！农场 [${farmId}] 已全员改种为 [${cropName}]。`);
        res.json({ status: "success", message: "Backend database updated successfully!" });

    } catch (err) {
        console.error("🚨 写入数据库失败:", err);
        res.status(500).json({ status: "error", message: "服务器无法改写本地账本文件" });
    }
});

// ==================== 🏛️ ADMIN 专属接口 1：获取中央大屏宏观统计数据 ====================
app.get('/api/admin/summary', (req, res) => {
    console.log(`\n🛰️ [Admin网关] 正在计算全平台宏观运行简报...`);
    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        
        // 1. 统计注册总人数和活跃农民数
        const userSection = dbContent.split('[USERS]')[1].split('[FARMS]')[0];
        const userLines = userSection.split('\n').filter(line => line.trim() && !line.startsWith('['));
        
        let totalUsers = userLines.length;
        let activeFarmers = userLines.filter(line => line.split('|')[4] && line.split('|')[4].trim().toLowerCase() === 'farmer').length;
        
        // 2. 统计全平台农场总数
        const farmSection = dbContent.split('[FARMS]')[1].split('[PLOTS]')[0];
        const farmLines = farmSection.split('\n').filter(line => line.trim());
        let totalFarms = farmLines.length;

        // 3. 动态模拟系统健康度 (对齐Java Admin的95%-100%随机波动)
        let systemHealth = (95 + Math.random() * 5).toFixed(1);

        res.json({
            status: "success",
            systemHealth: systemHealth,
            totalUsers: totalUsers,
            totalFarms: totalFarms,
            activeFarmers: activeFarmers,
            topCrop: "Corn" // 默认主导作物
        });
    } catch (err) {
        res.status(500).json({ status: "error", message: "中央大屏数据提取失败" });
    }
});

// ==================== 🏛️ ADMIN 专属接口 2：拉取全用户集中管理列表 ====================
app.get('/api/admin/users', (req, res) => {
    console.log(`\n👥 [Admin网关] 正在调阅全员注册档案 [USERS]...`);
    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        const userSection = dbContent.split('[USERS]')[1].split('[FARMS]')[0];
        const lines = userSection.split('\n').filter(line => line.trim());

        let userList = lines.map(line => {
            const parts = line.split('|');
            return { id: parts[0], username: parts[1], email: parts[3], role: parts[4] };
        });

        res.json({ status: "success", users: userList });
    } catch (err) {
        res.status(500).json({ status: "error", message: "全员档案读取失败" });
    }
});

// ==================== 🏛️ ADMIN 专属接口 3：拉取全跨国农场监控总表 ====================
app.get('/api/admin/farms', (req, res) => {
    console.log(`\n🏢 [Admin网关] 正在扫描全球注册农场总表 [FARMS]...`);
    try {
        const dbPath = path.join(__dirname, 'database.txt');
        const dbContent = fs.readFileSync(dbPath, 'utf-8');
        
        const farmSection = dbContent.split('[FARMS]')[1].split('[PLOTS]')[0];
        const farmLines = farmSection.split('\n').filter(line => line.trim());

        // 建立一个简单的用户映射表，用来查出农场的 OWNER 到底是谁
        const userSection = dbContent.split('[USERS]')[1].split('[FARMS]')[0];
        const userLines = userSection.split('\n').filter(line => line.trim());
        let userMap = {};
        userLines.forEach(line => { userMap[line.split('|')[0]] = line.split('|')[1]; });

        let globalFarmList = farmLines.map(line => {
            const parts = line.split('|');
            return {
                farmId: parts[0],
                farmName: parts[1],
                ownerName: userMap[parts[2]] || "Unknown" // 查出所有者名字
            };
        });

        res.json({ status: "success", farms: globalFarmList });
    } catch (err) {
        res.status(500).json({ status: "error", message: "全球农场扫描失败" });
    }
});

// ==================== 🚀 服务器启动监听（必须在最最最底部！） ====================
app.listen(3000, () => {
    console.log("🚀 全栈大满贯中端网关全线启动！正在监听 3000 端口...");
});