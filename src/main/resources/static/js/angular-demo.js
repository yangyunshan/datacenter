/**
 * Created by baukh on 18/4/11.
 */

angular.module("myApp", ['gridManager'])
.controller('AppController', ['$window', '$rootScope', '$timeout', '$scope', '$element', '$sce', '$gridManager', function($window, $rootScope, $timeout, $scope, $element, $sce, $gridManager) {
    $scope.testClick = (row) => {
        console.log('click', row);
    };
    // 表格唯一标识
    $scope.gridManagerName = 'testAngular';

    // 博文类型
    $scope.TYPE_MAP = {};
    $scope.TYPE_LIST = [
        {value: '1', text: '平台'},
        {value: '2', text: '传感器'},
    ];
    $scope.TYPE_LIST.forEach(item => {
        $scope.TYPE_MAP[item.value] = item.text;
    });

    // 公开方法列表
    $scope.GM_PUBLISH_METHOD_MAP =  {
        get: {
            key: 'get',
            relyInit: true,  // 是否依赖init方法
            title: '获取表格的实时配置信息',
            code: `$gridManager.get('${$scope.gridManagerName}');`
        },
        version: {
            key: 'version',
            relyInit: false,
            title: '获取当前GridManager的版本号',
            code: '$gridManager.version;'
            // code: 'angular.module("gridManager").version;'
        },
        getLocalStorage: {
            key: 'getLocalStorage',
            relyInit: true,
            title: '获取表格用户记忆',
            code: `$gridManager.getLocalStorage('${$scope.gridManagerName}');`
        },
        resetLayout: {
            key: 'resetLayout',
            relyInit: true,
            title: '重置表格布局',
            code: `$gridManager.resetLayout('${$scope.gridManagerName}', '800px', '500px');`
        },
        clear: {
            key: 'clear',
            relyInit: true,
            title: '清除表格记忆数据',
            code: `$gridManager.clear('${$scope.gridManagerName}');`
        },
        getRowData: {
            key: 'getRowData',
            relyInit: true,
            title: '获取指定tr所使用的数据',
            code: `$gridManager.getRowData('${$scope.gridManagerName}', document.querySelector("table[grid-manager=${$scope.gridManagerName}] tbody tr"));`
        },
        updateRowData: {
            key: 'updateRowData',
            relyInit: true,
            title: '更新指定行所使用的数据',
            code: `$gridManager.updateRowData('${$scope.gridManagerName}', 'id', {id: 92, title: 'ccc'});`
        },
        setSort: {
            key: 'setSort',
            relyInit: true,
            title: '手动设置排序',
            code: `$gridManager.setSort('${$scope.gridManagerName}', {createDate: 'ASC'});`
        },
        setConfigVisible: {
            key: 'setConfigVisible',
            relyInit: true,
            title: '设置表头配置区域可视状态',
            code: `$gridManager.setConfigVisible('${$scope.gridManagerName}', true);`
        },
        showTh: {
            key: 'showTh',
            relyInit: true,
            title: '设置列为可视状态',
            code: `$gridManager.showTh('${$scope.gridManagerName}', 'pic');`
        },
        hideTh: {
            key: 'hideTh',
            relyInit: true,
            title: '设置列为隐藏状态',
            code: `$gridManager.hideTh('${$scope.gridManagerName}', 'pic');`
        },
        exportGridToXls: {
            key: 'exportGridToXls',
            relyInit: true,
            title: '导出指定表格',
            code: `$gridManager.exportGridToXls('${$scope.gridManagerName}', 'demo中使用的导出');`
        },
        setQuery: {
            key: 'setQuery',
            relyInit: true,
            title: '更改在生成组件时所配置的参数query',
            code: `$gridManager.setQuery('${$scope.gridManagerName}', {'userName':'baukh','sex':'男'});`
        },
        setAjaxData: {
            key: 'setAjaxData',
            relyInit: true,
            title: '用于再次配置ajaxData数据',
            code: `$gridManager.setAjaxData('${$scope.gridManagerName}', {data: [], totals: 0});`
        },
        refreshGrid: {
            key: 'refreshGrid',
            relyInit: true,
            title: '刷新表格',
            code: `$gridManager.refreshGrid('${$scope.gridManagerName}');`
        },
        getCheckedTr: {
            key: 'getCheckedTr',
            relyInit: true,
            title: '获取当前选中的行',
            code: `$gridManager.getCheckedTr('${$scope.gridManagerName}');`
        },
        getCheckedData: {
            key: 'getCheckedData',
            relyInit: true,
            title: '获取选中行的渲染数据',
            code: `$gridManager.getCheckedData('${$scope.gridManagerName}');`
        },
        setCheckedData: {
            key: 'setCheckedData',
            relyInit: true,
            title: '设置选中的数据',
            code: `$gridManager.setCheckedData('${$scope.gridManagerName}', []);`
        },
        cleanData: {
            key: 'cleanData',
            relyInit: true,
            title: '清除指定表格数据',
            code: `$gridManager.cleanData('${$scope.gridManagerName}');`
        },
        destroy: {
            key: 'destroy',
            relyInit: true,
            title: '消毁指定的GridManager实例',
            code: `$gridManager.destroy('${$scope.gridManagerName}');`
        }
    };

    // 当前选中的公开方法
    $scope.fnSelected = '-1';

    // 公开方法code
    $scope.fnCode = '';

    // 切换执行方法事件
    $scope.onFnChange = fnSelected => {
        $scope.fnSelected = fnSelected;
        $scope.fnCode = $scope.GM_PUBLISH_METHOD_MAP[fnSelected] ? $scope.GM_PUBLISH_METHOD_MAP[fnSelected].code : '';
    };

    // 当前是否已经实例化
    $scope.inited = false;

    // 执行方法事件
    $scope.onFnRun = () => {
        if ($scope.fnSelected === '-1') {
            return;
        }
        const selectedFN = $scope.GM_PUBLISH_METHOD_MAP[$scope.fnSelected];
        try {
            const log = eval($scope.fnCode);
            console.group(selectedFN.key);
            console.log($scope.fnCode);
            console.log(log);
            console.groupEnd();
            if (selectedFN.key === 'init') {
                $scope.inited = true;
            }
            if (selectedFN.key === 'destroy') {
                $scope.inited = false;
            }
            $scope.fnRunInfo = $sce.trustAsHtml(`<span class="success-info">
                    <a href="http://gridmanager.lovejavascript.com/api/index.html#${selectedFN.key}" target="_blank">${selectedFN.key}</a>
                    执行成功, 请打开控制台查看具体信息
                </span>`);
        } catch (e) {
            $scope.fnRunInfo = $sce.trustAsHtml(`<span class="error-info">
                    <a href="http://gridmanager.lovejavascript.com/api/index.html#${selectedFN.key}" target="_blank">${selectedFN.key}</a>
                    执行失败, 请打开控制台查看具体信息
                </span>`);
            console.error('执行错误: ', e);
        }
    };

    // 搜索
    $scope.searchForm = {
        title: '',
        type: '-1',
        content: ''
    };

    /**
     * 搜索事件
     */
    $scope.onSearch = () => {
        console.log('onSearch');
        $gridManager.setQuery('testAngular', $scope.searchForm);
    };

    $scope.onReset = () => {
        $scope.searchForm = {
            title: '',
            type: '-1',
            content: ''
        };
    };

    // 表格渲染回调函数
    // query为gmOptions中配置的query
    $scope.callback = () => {
        $timeout(() => {
            $scope.inited = true;
            $scope.$digest();
        }, 100);
    };

    $scope.option = {
        gridManagerName: $scope.gridManagerName,
        width: '100%',
        height: '100%',
        supportAjaxPage: true,
        isCombSorting: true,
        disableCache: false,
        ajaxData: function () {
            return "../datacenter/getAllSensorInfo";
        },
        ajaxType: 'GET',

        columnData: [
            {
                key: 'sensorId',
                remind: 'the id',
                // align: 'left',
                text: '标识符',
                align: 'center',
                width: '150px',
                sorting: 'sensorId',
            },{
                key: 'name',
                remind: 'the name',
                text: '名称',
                align: 'center',
                width: '150px',
                sorting: '',
            },{
                key: 'description',
                remind: 'the info',
                align: 'center',
                width: '300px',
                text: '简介'
            },{
                key: 'platformName',
                remind: 'the username',
                align: 'center',
                width: '200px',
                text: '平台',

            },{
                key: 'username',
                remind: 'the username',
                align: 'center',
                width: '100px',
                text: '作者',
                // 使用函数返回 dom string
                template: `<a class="plugin-action" href="https://github.com/baukh789" target="_blank" title="去看看{{row.username}}的github">{{row.username}}</a>`
            },{
                key: 'createDate',
                width: '130px',
                text: '创建时间',
                sorting: 'DESC',
                // 使用函数返回 htmlString
                template: function(createDate, rowObject){
                    return new Date(createDate).toLocaleDateString();
                }
            },{
                key: 'lastDate',
                width: '130px',
                text: '最后修改时间',
                sorting: '',
                // 使用函数返回 htmlString
                template: function(lastDate, rowObject){
                    return new Date(lastDate).toLocaleDateString();
                }
            },{
                key: 'action',
                remind: 'the action',
                // width: '100px',
                align: 'center',
                text: '<span style="color: red">操作</span>',
                // 直接返回 htmlString
                template: '<span class="plugin-action" ng-click="detailInfo(row, index)">查看详细信息</span>' +
                    '<span class="plugin-action" ng-click="obsInfo(row, index)">查看观测数据</span>'
            }
        ]
    };

    $scope.obsInfo = function(row, index) {
        var id = row.sensorId;

        $.ajax({
            url: '../datacenter/observation/getObservationByProcedure/'+id,
            type: "get",
            dataType: 'json',
            success: function (data) {
                if (data!=null) {
                    console.log(data);
                    $("#Sensor tbody").empty();
                    $.each(data,function(index,item){
                        var identifier =$("<td></td>").append(item.outId);
                        var description =$("<td></td>").append(item.description);
                        var observedProperty =$("<td></td>").append(item.observedProperty);
                        var time =$("<td></td>").append(item.time);
                        var type =$("<td></td>").append(item.type);
                        var foiId =$("<td></td>").append(item.foiId);
                        var btnTd = $("<button></button>").addClass("btn btn-primary btn-sm btn").append($("<span></span>")).addClass("glyphicon glyphicon-search").append("");
                        btnTd.attr("obs_value",item.value);
                        // btnTd.attr(id, "obs");

                        $("<tr></tr>")
                            .append(identifier)
                            .append(description)
                            .append(foiId)
                            .append(observedProperty)
                            .append(time)
                            .append(type)
                            .append(btnTd)
                            .appendTo("#Sensor tbody");

                    });
                    //弹出模态框
                    $("#SensorModal").modal({
                        backdrop:"static"
                    });


                }
            }

        })
    };

    $scope.detailInfo = function(row, index) {
        var id = row.sensorId;
    };
}]);
