

hzWikitaxonomy
==============
>this is a project about wikipedia
>created by hanzhe, Peking University

TimeTable

    1.抽取三元组<b>（v1.1，20140819）</b>
    2.predicate正规化
        a.特征抽取
            (1)upper_title信息提取
            (2)link信息提取
            (3)object信息提取
            (4)category信息提取
            (5)wikitext_predicate 对应
        b.Issue
            (1)相同的content是否可能指不同的predicate
            (2)同一篇文章是否可能出现多个相同的predicate，但是表达不同的信息


### TODO list


============

20141023

    加入谓词特征
    1.标记客体NER，然后分配给谓词

    抽取三元组    
1.<del>[page 1631973]: 主要聚集地 南非 4000人， 南非被识别为谓词</del>
    2.<del>[page 1649658 1649658]: 发现小行星数量，整个表格都是小行星列表，无用</del>   
    4.<del>[page 166876]: 宾语含有{{{twin1_country}}}去掉</del>
    <del>5.[page 167439]: 宾是×××px,去掉
    6.【page 201598】：infobox是“站点和里程”，去掉
    7.【page 19944】：族群做谓词，改正
    8.【page 210136】：查论编
    9.【page 218888】：谓词含有空格且前后都是中文or标点，去掉空格
    10.【page 234809】：分布地区，国家变谓词了
    11.【page 261509】：人种构成，语言都要被替换
    12.【page 26477 3394357】：表格，不是三元组
    13【page 275026， 2958861 3369864】：上级标题是仪器，望远镜，不是三元组
    14.含有← ↙ → ◄， ►都不可能是三元组，check：2883611 2883614 2883590 2880754 3019905 3252031 3259367
    15【page 2816121， 2933547 3184078 3202797】：分布地区</del>
   
    16.【page 2824 3103351 3115327 3140872 3132572 3324026 3637016】：错误的商机标题，41任美国总统
    17.【page 2828】：上级标题不对，加粗的字体不能做三元组的谓词
    18.【page 2939419】：上级标题：机构沿革
    19.【page 2952445】：非正确的上级标题
    20.”第×××任×××“不是上级标题
    21.【page 2962115】出现<hr />上级标题清空
    22.【page 2963968】第一行标题不是上级标题
    23.【page 2971068】数字不做谓词，俱乐部信息不做三元组
    24.【page 25451 29905 3151942】上级标题：主要品种
    25.【page 299399】tr：small
    26.【page 2998779】上级标题：伤亡与损失，不是三元
    27.[page 304782]
    28.[page 3050145]得奖记录去掉
    29.[page 311848]上级标题：承建商
    30.[page 3151422 3223283 3224391 3352843] 2014-今
    31.[page 3167608] 奖项与成就
    32.[page 3166845] 开头"<"
    33.[page 3481350 409657 411419] infobox nounderlines 不是三元组
    34.【page 3538759]
    35.[page 358148]指挥官和领导者
    36.[page 411338 425512]人口 -2000年
    37.[page 424805]友好城市




20140901 

    1. PageIdTitles 表有错，需要重抽：pageid:青岛四方机车车辆 1454802 在抽的表里有另一个id：1843321

    
20140827 TODO list

    1. (done)predicate bug fixes: pageid: 96738, 1001309
    2. (done)bug:如果predicate含有“：”，则应该自成一条三元组
    3. (done)检查是否去掉“No. 11 火箭队”这样的sub-title:
        pageid: 勒布朗詹姆斯 136558


20140824 TODO list
    
    1. word2vec 重新训练：min_count 多少？设置自己的词典？
    2. (done)抽取triples判断predicate，如果是当前title，则需要取消（减少冗余的predicate）
    3. (done)predicate 需要变成多个：pageid = 3033418, 1000090
    4. (done)★ ☆ predicate如果是数字，要采用subtitle作为predicate ☆ ★ 

0. (done) <del>在一个页面中不同的predicate可能链向同一个实体</del> <br />
        pageid:100120 近日点和远日点都链向“拱点”
        需要改变triples中predicate的存储格式，只能存content了！！

1. (done) <del>single line in infobox, like this </del>

         外部链接
        [官方网站]
        [IMDb介绍]
        [TV.com介绍]
        
        may be formed like this:
        
        pageid	官方网站	官方网站.link
        pageid  IMDb介绍	IMDb介绍.link
        pageid	TV.com介绍	TV.com介绍.link

2. 如果是奖牌信息，还没想到好的处理方法,比如：pageid：1000521 郭跃华

          世界杯乒乓球赛
        金牌    1980年香港  男子单打

## statistic calculate

triples from wikitext:

    1.entityNr: 396517
    2.category line nr: 1598051

triples from web:

    1.entityNr: 375104
    2.total triple Nr: 3785065


## UPdate

<b>update 20140827:</b>

    完成了<b>predicate的正规化</b>，抽取的三元组基本都是正确的（花了不少时间处理特别的格式），下面正式开始实验阶段了
    > 在linux上部署好了zhwiki_dumps_20140823（以后可以快速导入到笔记本，备份到了csdn上）
    > 

update 20140819:

    1.修复抽取infobox的bug（提升到v1.1），不会把页面底部的table错误抽出
    2.开始predicate特征提取（3/5），完成link，content，upper_title信息的抽取工作

update 20140805:

    1.开始更新README.md
    2.暂停predicate，object情况讨论
    3.抽取triple，修复bug

20140807:

    1.抽取infobox的table还有问题
        a.有一些在页面底部的非infobox被错误抽取为infobox：1023117
        b.有一些页面没有抽到infobox（table格式不标准）：很多

20140818:

    1.开始整体抓取，暂时停止对html页面的抽取，进行下面的操作
        a.之后再修改抽取问题
        b.目前问题：
            1.“_/_”应该是多行，但是只弄成一条三元组
    2.目前抓取效果
        第一个文件夹275个含infobox的实体，抓取3397行
