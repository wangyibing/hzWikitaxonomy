

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

20140929

    减少谓词
    1. predicate还应该是每行一个，不允许一个tr标签生成多个predicate
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
