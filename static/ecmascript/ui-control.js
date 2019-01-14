var app = new Vue({
    el: '#app',
    data: {
        mutexLock: false,
        graph: null,
        loading: false,
        router: 'index',
        jsonVertex: [],
        jsonEdge: [],
        shortestLength: 0,
        shortestSource: 1,
        shortestTarget: 2,
        shortestPaths: [],
        recommendSource: 0,
        recommendTarget: 0,
        searchResults: [],
        hamiltonLength: 0,
        detailDisplaySpot: {},
        parkingSize: 5,
        waiting: [],
        parking: [],
        buffer: [],
        parkingMsg: "",
        cardIDToAdd: "",
        notice: "",
        admin:{
            updateEdge: {},
            updateVertex: {},
            newEdge:{
                from: '',
                to: '',
                weight: 0,
                time: 0
            },
            newVertex:{
                hasRestArea:false,
                hasToilet:false,
                name:"",
                description:"",
                id: 0,
                popular: 0
            },
            newToConnect: 0
        }
    },
    methods: {
        lock: function (){
            this.mutexLock = true;
        },
        unlock: function (){
            this.mutexLock = false;
        },
        findIn: function (array, id) {
            for (var i = 0; i < array.length; i++) {
                if (array[i].id == id) {
                    return array[i]
                }
            }
        },
        findVertex: function (id) {
            for (var i = 0; i < this.jsonVertex.length; i++) {
                if (this.jsonVertex[i].id == id) {
                    return this.jsonVertex[i]
                }
            }
        },
        newEdge: function () {
            if(this.admin.newEdge.weight==""||this.admin.newEdge.time==""||this.admin.newEdge.from==this.admin.newEdge.to){
                mdui.alert('不正确的输入');
            }else{
                if(!this.admin.newEdge.hasOwnProperty("id")) {
                    for (var i = 0; i < this.jsonEdge.length; i++) {
                        if (this.jsonEdge[i].from == this.admin.newEdge.from && this.jsonEdge[i].to == this.admin.newEdge.to) {
                            mdui.alert('不可添加重复的路径。');
                            return;
                        }
                    }
                }
                UIEngine.newPath(JSON.stringify(this.admin.newEdge));
                mdui.alert('路径已保存。')
            }
        },
        newVertex: function () {
            if(this.admin.newVertex.name==""||this.admin.newVertex.description==""){
                mdui.alert('不正确的输入。');
            }else{
                if(!this.admin.newVertex.hasOwnProperty("id")) {
                    for (var i = 0; i < this.jsonVertex.length; i++) {
                        if (this.jsonVertex[i].name == this.admin.newVertex.name) {
                            mdui.alert('此名称的景点已存在。');
                            return;
                        }
                    }
                }
                UIEngine.newSpot(JSON.stringify({vertex: this.admin.newVertex, newToConnect: this.admin.newToConnect}));
            }
        },
        byPopularA: function(a,b){
            return a.popular-b.popular
        },
        byPopularD: function(a,b){
            return b.popular-a.popular
        }
    }
});

Array.prototype.myRemove = function (dx) {
    if (isNaN(dx) || dx > this.length) {
        return false;
    }
    this.splice(dx, 1);
};

var UIControl = {
    graph: null,
    vertex: [],
    edge: [],
    initData: function (jsonString) {
        var message = JSON.parse(jsonString);
        app.jsonVertex = message.ScenicSpot;
        app.jsonEdge = message.ScenicPath;
        app.notice = message.Notice[0].content;
    },
    drawGraph: function (targetElementID) {
        var option = {
            tooltip: {},
            animationDurationUpdate: 500,
            animationEasingUpdate: 'quinticInOut',
            color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                    offset: 0, color: '#4059a9' // 0% 处的颜色
                }, {
                    offset: 1, color: '#5055a9' // 100% 处的颜色
                }],
                globalCoord: false // 缺省为 false
            },
            series: [
                {
                    type: 'graph',
                    layout: 'force',
                    nodeScaleRatio: '0.5',
                    animation: true,
                    force: {
                        gravity: 0.5,
                        repulsion: 20,
                        edgeLength: [5, 240]
                    },
                    label: {
                        normal: {
                            show: true
                        }
                    },
                    symbolSize: 40,
                    roam: true,
                    edgeLabel: {
                        normal: {
                            textStyle: {
                                fontSize: 15
                            }
                        }
                    },
                    data: this.vertex,
                    edges: this.edge
                }
            ]
        };
        setTimeout(function () {
            var graphContainer = document.getElementById(targetElementID);
            graphContainer.clientWidth = graphContainer.parentElement.clientWidth;
            app.graph = echarts.init(graphContainer);
            app.graph.setOption(option);
        }, 200);
    },
    addGraphVertex: function (id, name) {
        this.vertex.push({
            symbol: 'pin',
            name: name,
            value: id
        });
    },
    addGraphEdge: function (source, target, weight) {
        this.edge.push({
            source: source,
            target: target,
            value: weight,
            label: {
                show: true,
                formatter: '{c}m',
                fontSize: 15
            }
        });
    },
    emphasizeEdge: function (source, target) {
        if (app.shortestPaths.length == 0) {
            app.shortestPaths.push(source)
        }
        app.shortestPaths.push(target);
        for (var i = 0; i < this.edge.length; i++) {
            if ((this.edge[i].source === source && this.edge[i].target === target) || (this.edge[i].source === target && this.edge[i].target === source)) {
                var weight = this.edge[i].value;
                this.edge[i] = {
                    source: source,
                    target: target,
                    value: weight,
                    lineStyle: {
                        color: 'red',
                        curveness: 0.1,
                        width: 5
                    },
                    label: {
                        show: true,
                        formatter: '距离{c}m',
                        fontSize: 10,
                        fontColor: 'red'
                    }
                }
            }
        }
    },
    unemphasizeEdge: function (source, target) {

        for (var i = 0; i < this.edge.length; i++) {
            if (this.edge[i].source === source && this.edge[i].target === target) {
                var weight = this.edge[i].value;
                this.edge[i] = {
                    source: source,
                    target: target,
                    value: weight
                }
            }
        }
        this.refreshGraph();
    },
    resetEmphasis: function () {
        app.shortestPaths = [];
        for (var i = 0; i < this.edge.length; i++) {
            var weight = this.edge[i].value;
            var source = this.edge[i].source;
            var target = this.edge[i].target;
            this.edge[i] = {
                source: source,
                target: target,
                value: weight
            }
        }
        this.refreshGraph();
    },
    resetGraph: function(){
        this.vertex = [];
        this.edge = [];
        this.refreshGraph();
    },
    refreshGraph: function () {
        app.graph.setOption({
            series: [{
                roam: true,
                data: this.vertex,
                edges: this.edge
            }]
        });
    }
};

var ParkingControl = {
    span: 250,
    count: 0,
    addToParking: function (json) {
        setTimeout(function () {
            app.parking.push(JSON.parse(json))
        }, this.count * this.span);
        this.count++;
    },
    addToWaiting: function (json) {
        setTimeout(function () {
            app.waiting.push(JSON.parse(json))
        }, this.count * this.span);
        this.count++;
    },
    addToBuffer: function (json) {
        setTimeout(function () {
            app.buffer.push(JSON.parse(json))
        }, this.count * this.span);
        this.count++;
    },
    removeFrom: function (array, id) {
        setTimeout(function () {
            for (var i = 0; i < array.length; i++) {
                if (array[i].id === id) {
                    array.myRemove(i);
                }
            }
        }, this.count * this.span);
        this.count++;
    }
}

function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZ2345678'; // 随机字符集
    var maxPos = $chars.length;
    var pwd = '';
    for (i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

var I64BIT_TABLE =
    '0123456789'.split('');

function hash(input){
    var hash = 5381;
    var i = input.length - 1;
    if(typeof input == 'string'){
        for (; i > -1; i--)
            hash += (hash << 5) + input.charCodeAt(i);
    }
    else{
        for (; i > -1; i--)
            hash += (hash << 5) + input[i];
    }
    var value = hash & 0x7FFFFFFF;

    var retValue = '';
    do{
        retValue += I64BIT_TABLE[value & 0x3F];
    }
    while(value >>= 6);

    return retValue;
}

Vue.prototype.hash = function (input) {
    hash(input)
}
