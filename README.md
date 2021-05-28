# 消息中心服务接口说明 v1.0.0

编写说明：

1. 文档版本号请与服务版本号保持一致；
2. 概述和主要功能部分由产品或架构编写；
3. 接口部分(三级目录)按模块(二级目录)进行归类，由开发人员按模板要求的格式编写。

## 目录

- [概述](#概述)
    - [主要功能](#主要功能)
    - [通讯方式](#通讯方式)
- [消息模块](#消息模块)
    - [生成短信验证码](#生成短信验证码)
    - [验证短信验证码](#验证短信验证码)
    - [发送标准消息](#发送标准消息)
    - [发送自定义消息](#发送自定义消息)
    - [获取用户消息列表](#获取用户消息列表)
    - [获取用户消息详情](#获取用户消息详情)
    - [删除用户消息](#删除用户消息)
- [任务模块](#任务模块)
    - [获取计划任务列表](#获取计划任务列表)
    - [获取计划任务详情](#获取计划任务详情)
    - [新增计划任务](#新增计划任务)
    - [立即执行任务](#立即执行任务)
    - [删除计划任务](#删除计划任务)
    - [禁用计划任务](#禁用计划任务)
    - [启用计划任务](#启用计划任务)
- [模板管理](#模板管理)
    - [获取消息模板列表](#获取消息模板列表)
    - [获取消息模板详情](#获取消息模板详情)
    - [新增消息模板](#新增消息模板)
    - [编辑消息模板](#编辑消息模板)
    - [删除消息模板](#删除消息模板)
    - [禁用消息模板](#禁用消息模板)
    - [启用消息模板](#启用消息模板)
- [场景管理](#场景管理)
    - [获取消息场景列表](#获取消息场景列表)
    - [获取消息场景详情](#获取消息场景详情)
    - [新增消息场景](#新增消息场景)
    - [编辑消息场景](#编辑消息场景)
    - [删除消息场景](#删除消息场景)
- [场景配置管理](#场景配置管理)
    - [获取场景配置列表](#获取场景配置列表)
    - [添加场景配置](#添加场景配置)
    - [移除场景配置](#移除场景配置)
- [DTO类型说明](#DTO类型说明)

## 概述

Insight
消息中心提供消息发送和计划任务调度的能力。用户可以根据需求，发送标准化的或者自定义的本地消息、通知和短信。计划任务功能则作为异步调用的补偿机制存在，为意外失败的调用提供自动重试机制。消息中心同时也提供短信验证码的生成和验证功能。
> 注：通知和短信的发送渠道需要自行对接。

### 主要功能

1. 短信验证码的生成和验证
2. 根据消息场景自动适配消息模板发送消息(本地消息|通知|短信)
3. 发送自定义消息(本地消息|通知|短信)
4. 计划任务管理
5. 消息模板管理
6. 消息场景管理
7. 消息场景配置管理，为场景配置不同的模板和参数

### 通讯方式

接口支持 **HTTP/HTTPS** 协议的 **GET/POST/PUT/DELETE** 方法，支持 **URL Params** 、 **Path Variable** 或 **BODY** 传递接口参数。如使用 **BODY**
传参，则需使用 **Json** 格式的请求参数。接口 **/URL** 区分大小写，请求以及返回都使用 **UTF-8** 字符集进行编码，接口返回的数据封装为统一的 **Json** 格式。

文档中所列举的类型皆为 **Java** 语言的数据类型，其它编程语言的的数据类型请自行对应。格式详见：[Reply数据类型](#Reply)。

建议在HTTP请求头中设置以下参数：

|参数名|参数值|
| ------------ | ------------ |
|Accept|application/json|
|Content-Type|application/json|

## 消息模块

### 生成短信验证码

根据指定的参数生成一个短信验证码并发送。

请求方法：**POST**

接口URL：**/base/message/v1.0/smscodes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|Integer|type|是|验证码类型:0.验证手机号;1.注册用户账号;2.重置密码;3.修改支付密码;4.修改手机号|
|String|mobile|是|手机号|
|Integer|length|是|验证码长度,建议4-8位|
|Integer|minutes|是|验证码有效时间(分钟)|

请求参数示例：

```json
{
  "type": 0,
  "mobile": "13958085808",
  "length": 4,
  "minutes": 15
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 验证短信验证码

验证短信验证码是否正确。验证通过返回调用成功，否则返回验证码错误的错误信息。

请求方法：**GET**

接口URL：**/base/message/v1.0/smscodes/{key}/status**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|key|是|验证参数,MD5(type + mobile + code)|
|Boolean|isCheck|否|是否检验模式,检验模式不会使验证码失效.默认为true|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/smscodes/fec92254fd0ecc1cee7f568f5b0a44f7/status?isCheck=false" \
 -H 'Accept: application/json' \
 -H 'Accept-Encoding: gzip, identity' \
 -H 'Content-Type: application/json'

```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 发送标准消息

发送一条标准消息，只需指定消息场景和合作伙伴(可选)，系统会自动匹配消息模板生成消息并发送给指定的接收人或进行公告。

请求方法：**POST**

接口URL：**/base/message/v1.0/messages**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|sceneCode|场景编码|
|String|partnerCode|合作伙伴编码|
|String|receivers|接收人,多个接收人使用逗号分隔|
|Map\<String, Object>|params|自定义参数|
|Boolean|isBroadcast|是否广播消息|

请求参数示例：

```json
{
  "scene": "0001",
  "receivers": "13958085808",
  "param": {
    "code": "123456",
    "minutes": 5
  },
  "isBroadcast": false
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 发送自定义消息

发送一条自定义消息给指定的接收人或进行公告。

请求方法：**POST**

接口URL：**/base/message/v1.0/customs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|tag|是|消息标签|
|Integer|type|是|发送类型:0.未定义;1.仅消息;2.仅通知;3.推送+消息;4.仅短信;8.仅邮件|
|String|receivers|是|接收人,多个接收人使用逗号分隔|
|String|title|是|消息标题|
|String|content|是|消息内容|
|Map\<String, Object>|params|否|自定义参数|
|Boolean|isBroadcast|是|是否广播消息|
|Date|expireDate|否|消息失效日期|

请求参数示例：

```json
{
  "tag": "验证邮件",
  "type": 1,
  "receivers": "abc@insight.com",
  "title": "验证邮件",
  "content": "亲爱的用户，请点击https://www.insight.com/verify/2214d验证您的邮箱。请勿回复此邮件，谢谢。",
  "broadcast": false
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 获取用户消息列表

可根据关键词查询当前用户的站内消息，仅限当前租户(可为空)和当前应用的消息。关键词精确匹配消息标签，模糊匹配消息标题。

请求方法：**GET**

接口URL：**/base/message/v1.0/messages**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据集合类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|消息ID|
|String|tag|消息标签,如"平台通知"/"系统消息"|
|String|title|消息标题|
|Boolean|read|是否已读|
|String|creator|创建人|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/messages?keyword=a" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY2ZmZlMTRlYTcwYTQ4MTJhZjdhZjMwOGUyZjJlNmRhIiwic2VjcmV0IjoiNTMwZDA3OTFiOTgxNGIwODg0NmY3MjQ2MjczNTUyZTcifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "7ab89a1bace84e5aadca44851901df02",
      "tag": "测试消息",
      "title": "你好a",
      "creator": "系统管理员",
      "createdTime": "2019-10-24 18:15:26",
      "read": true
    }
  ],
  "option": 1
}
```

[回目录](#目录)

### 获取用户消息详情

获取指定ID的消息详情，读取数据后该消息将被标记为已读。

请求方法：**GET**

接口URL：**/base/message/v1.0/messages/{id}**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|消息ID|

接口返回数据集合类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|消息ID|
|String|tag|消息标签,如"平台通知"/"系统消息"|
|String|title|消息标题|
|String|content|消息内容|
|Boolean|read|是否已读|
|Boolean|broadcast|是否广播消息|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/messages/7ab89a1bace84e5aadca44851901df02" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY2ZmZlMTRlYTcwYTQ4MTJhZjdhZjMwOGUyZjJlNmRhIiwic2VjcmV0IjoiNTMwZDA3OTFiOTgxNGIwODg0NmY3MjQ2MjczNTUyZTcifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "506b3e4c705d4861af91a8db1d729673",
    "tag": "测试消息",
    "title": "你好",
    "content": "你好",
    "creator": "系统管理员",
    "creatorId": "00000000000000000000000000000000",
    "createdTime": "2019-10-24 17:48:24",
    "broadcast": false,
    "read": true
  },
  "option": null
}
```

[回目录](#目录)

### 删除用户消息

获取指定ID的消息详情，读取数据后该消息将被标记为已读。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/messages**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息ID|

请求参数示例：

```json
"506b3e4c705d4861af91a8db1d729673"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## 任务模块

### 获取计划任务列表

根据关键词(可选)分页查询当前的计划任务列表。关键词可精确匹配任务类型(type)或调用方法(method)。

请求方法：**GET**

接口URL：**/base/message/v1.0/schedules**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词,精确匹配任务类型(type)或调用方法(method)|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据集合类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|任务ID|
|Integer|type|任务类型:0.消息发送;1.本地调用;2.远程调用|
|String|method|调用方法|
|Date|taskTime|下次执行时间|
|Integer|count|失败次数|
|Boolean|invalid|是否失效|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/schedules" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUwYWMzMjgwY2YzODQwMDQ5MGVlNDExOTdlN2YwOTVjIiwic2VjcmV0IjoiYjE3MTYwNGJhN2VkNGJjMjkzOGI2NTE2NWM3OTBjZmIifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "13b09fd03a5a47b7918b24ec42c3db06",
      "type": 1,
      "method": "getConfigs",
      "taskTime": "2019-10-04 21:25:10",
      "count": 4,
      "createdTime": "2019-10-04 21:24:11",
      "invalid": false
    }
  ],
  "option": 1
}
```

[回目录](#目录)

### 获取计划任务详情

获取指定ID的计划任务。

请求方法：**GET**

接口URL：**/base/message/v1.0/schedules/{id}**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|任务ID|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|任务ID|
|Integer|type|任务类型:0.消息发送;1.本地调用;2.远程调用|
|String|method|调用方法|
|Date|taskTime|下次执行时间|
|\<T>|content|任务内容,泛型|
|Integer|count|失败次数|
|Boolean|invalid|是否失效|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/schedules/13b09fd03a5a47b7918b24ec42c3db06" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUwYWMzMjgwY2YzODQwMDQ5MGVlNDExOTdlN2YwOTVjIiwic2VjcmV0IjoiYjE3MTYwNGJhN2VkNGJjMjkzOGI2NTE2NWM3OTBjZmIifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "13b09fd03a5a47b7918b24ec42c3db06",
    "type": 1,
    "method": "getConfigs",
    "taskTime": "2019-10-04 21:27:35",
    "content": {
      "url": "/base/auth/v1.0/configs",
      "body": {
        "url": "/base/user/v1.0/users",
        "name": "获取用户列表",
        "limit": true,
        "method": "GET",
        "remark": null,
        "verify": true,
        "message": null,
        "authCode": null,
        "limitGap": 1,
        "limitMax": null,
        "limitCycle": null
      },
      "method": "POST",
      "headers": {
        "Accept": "application/json",
        "loginInfo": "eyJhcHBJZCI6IjlkZDk5ZGQ5ZTZkZjQ2N2E4MjA3ZDA1ZWE1NTgxMTI1IiwidGVuYW50SWQiOiIyNTY0Y2Q1NTljZDM0MGYwYjgxNDA5NzIzZmQ4NjMyYSIsImRlcHRJZCI6bnVsbCwidXNlcklkIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAiLCJ1c2VyTmFtZSI6Iuezu+e7n+euoeeQhuWRmCJ9",
        "requestId": "778c544a18174970bbd6907016eb0556",
        "Content-Type": "application/json"
      },
      "service": "base-auth"
    },
    "count": 8,
    "createdTime": "2019-10-04 21:24:11",
    "invalid": false
  },
  "option": null
}
```

[回目录](#目录)

### 新增计划任务

新增一个计划任务。

请求方法：**POST**

接口URL：**/base/message/v1.0/schedules**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|Integer|type|是|任务类型:0.消息发送;1.本地调用;2.远程调用|
|String|method|是|调用方法|
|Date|taskTime|否|下次执行时间|
|\<T>|content|是|任务内容,泛型([InsightMessage](#InsightMessage)\|[ScheduleCall](#ScheduleCall))|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|-|任务ID|

请求参数示例：

```json
{
  "type": 1,
  "method": "getConfigs",
  "content": {
    "method": "POST",
    "service": "base-auth",
    "url": "/base/auth/v1.0/configs",
    "headers": {
      "Accept": "application/json",
      "Content-Type": "application/json",
      "requestId": "778c544a18174970bbd6907016eb0556",
      "loginInfo": "eyJhcHBJZCI6IjlkZDk5ZGQ5ZTZkZjQ2N2E4MjA3ZDA1ZWE1NTgxMTI1IiwidGVuYW50SWQiOiIyNTY0Y2Q1NTljZDM0MGYwYjgxNDA5NzIzZmQ4NjMyYSIsImRlcHRJZCI6bnVsbCwidXNlcklkIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAiLCJ1c2VyTmFtZSI6Iuezu+e7n+euoeeQhuWRmCJ9"
    },
    "body": {
      "name": "获取用户列表",
      "method": "GET",
      "url": "/base/user/v1.0/users",
      "authCode": null,
      "limitGap": 1,
      "limitCycle": null,
      "limitMax": null,
      "message": null,
      "remark": null,
      "verify": true,
      "limit": true
    }
  }
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "13b09fd03a5a47b7918b24ec42c3db06",
  "option": null
}
```

[回目录](#目录)

### 立即执行任务

将指定ID的计划任务的下次执行时间改为当前时间。

请求方法：**PUT**

接口URL：**/base/message/v1.0/schedules**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|任务ID|

请求参数示例：

```json
"13b09fd03a5a47b7918b24ec42c3db06"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 删除计划任务

删除指定ID的任务。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/schedules**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|任务ID|

请求参数示例：

```json
"13b09fd03a5a47b7918b24ec42c3db06"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 禁用计划任务

禁用指定ID的任务，任务被禁用后不会被执行。

请求方法：**PUT**

接口URL：**/base/message/v1.0/schedules/disable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|任务ID|

请求参数示例：

```json
"13b09fd03a5a47b7918b24ec42c3db06"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 启用计划任务

启用指定ID的任务，任务被启用后会被正常执行。

请求方法：**PUT**

接口URL：**/base/message/v1.0/schedules/enable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|任务ID|

请求参数示例：

```json
"13b09fd03a5a47b7918b24ec42c3db06"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## 模板管理

### 获取消息模板列表

根据关键词(可选)分页查询当前租户的消息模板，关键词可精确匹配模板编号或模糊匹配模板标题。

请求方法：**GET**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|code|模板编号|
|String|tag|消息标签|
|Integer|type|发送类型:0.未定义;1.仅消息;2.仅推送;3.推送+消息;4.仅短信|
|String|title|消息标题|
|Integer|expire|消息有效时长(小时)|
|String|creator|创建人|
|Date|createdTime|创建时间|

请求示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/templates?keyword=0001" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjQ3OWNlZTRhZDY3MjQzYTJiYzgyNWQ1NTg4MjBiZTY1Iiwic2VjcmV0IjoiOWU1MDk1ZmQ5ZWUyNGRmZWEyMTMyOGNmMmNlZmQyMzAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "387e156ddc7211e9bc200242ac110004",
      "code": "0001",
      "tag": null,
      "type": 4,
      "title": "验证码登录",
      "expire": null,
      "creator": "系统",
      "createdTime": "2019-09-29 17:46:39"
    }
  ],
  "option": 1
}
```

[回目录](#目录)

### 获取消息模板详情

获取指定ID的消息模板详情。

请求方法：**GET**

接口URL：**/base/message/v1.0/templates/{id}**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|消息模板ID|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|tenantId|租户ID|
|String|code|模板编号|
|String|tag|消息标签|
|Integer|type|发送类型:0.未定义;1.仅消息;2.仅推送;3.推送+消息;4.仅短信;8:仅邮件|
|String|title|消息标题|
|String|content|消息内容模板|
|Integer|expire|消息有效时长(小时)|
|String|remark|备注|
|Boolean|invalid|是否失效|
|String|creator|创建人|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/templates/387e156ddc7211e9bc200242ac110004" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6Ijc0MDc0ZmJkZjVmODQ3ZWZhM2ZjZDY2NmNlOWIzYjViIiwic2VjcmV0IjoiNWU1MzQxNTQ1Y2IwNDIyODliZDljNzNlYjlmMDk3ODUifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "387e156ddc7211e9bc200242ac110004",
    "tenantId": null,
    "code": "0001",
    "tag": "短信验证码",
    "type": 4,
    "title": "验证码登录",
    "content": "[{code}]是您登录Insight系统的验证码,请在{minutes}分钟内使用【{sign}】",
    "expire": null,
    "remark": "Insight系统登录验证码",
    "creator": "系统",
    "creatorId": "00000000000000000000000000000000",
    "createdTime": "2019-09-29 17:46:39",
    "invalid": false
  },
  "option": null
}
```

[回目录](#目录)

### 新增消息模板

为当前租户新增一个消息模板

请求方法：**POST**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|tag|是|消息标签|
|Integer|type|是|发送类型:0.未定义;1.仅消息;2.仅推送;3.推送+消息;4.仅短信;8:仅邮件|
|String|title|是|消息标题|
|String|content|是|消息内容模板|
|Integer|expire|否|消息有效时长(小时)|
|String|remark|否|备注|

请求参数示例：

```json
{
  "tag": "邮件验证码",
  "type": 8,
  "title": "请验证您的邮箱",
  "content": "亲爱的{user}，请点击{url}验证您的邮箱。请勿回复此邮件，谢谢。"
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "8fe3a826a0b642e79c87766ae892f8e6",
  "option": null
}
```

[回目录](#目录)

### 编辑消息模板

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**PUT**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|UUID主键|
|String|tag|是|消息标签|
|Integer|type|是|发送类型:0.未定义;1.仅消息;2.仅推送;3.推送+消息;4.仅短信;8:仅邮件|
|String|title|是|消息标题|
|String|content|是|消息内容模板|
|Integer|expire|否|消息有效时长(小时)|
|String|remark|否|备注|

请求参数示例：

```json
{
  "id": "8fe3a826a0b642e79c87766ae892f8e6",
  "tag": "邮箱验证",
  "type": 8,
  "title": "请验证您的邮箱",
  "content": "亲爱的{user}，请点击{url}验证您的邮箱。请勿回复此邮件，谢谢。",
  "remark": "Insight系统验证邮件"
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 删除消息模板

删除指定ID的消息模板，仅限未配置到场景的模板。如模板已配置到场景，需先删除相应配置。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息模板ID|

请求参数示例：

```json
"8fe3a826a0b642e79c87766ae892f8e6"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 禁用消息模板

禁用指定ID的消息模板。

请求方法：**PUT**

接口URL：**/base/message/v1.0/templates/disable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息模板ID|

请求参数示例：

```json
"387e156ddc7211e9bc200242ac110004"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 启用消息模板

启用指定ID的消息模板。

请求方法：**PUT**

接口URL：**/base/message/v1.0/templates/enable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息模板ID|

请求参数示例：

```json
"387e156ddc7211e9bc200242ac110004"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## 场景管理

### 获取消息场景列表

根据关键词(可选)分页查询消息场景，关键词可精确匹配场景编号或模糊匹配场景名称。

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|code|场景编号|
|String|name|场景名称|
|String|remark|备注|
|String|creator|创建人|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/scenes?keyword=0001" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImY0YzUzZGU3ZjJkZDRlNDk5YWZlNzgyMTRhZTI0MWEyIiwic2VjcmV0IjoiYjQ4NzcxMTIwNDQzNDFjY2EwNjE2NWIxZDM1Y2ZjNmUifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "27c3a319dc7011e9bc200242ac110004",
      "code": "0001",
      "name": "验证码登录",
      "remark": null,
      "creator": "系统"
    }
  ],
  "option": 1
}
```

[回目录](#目录)

### 获取消息场景详情

获取指定ID的消息场景详情。

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes/{id}**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|消息场景ID|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|code|场景编号|
|String|name|场景名称|
|String|remark|备注|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/scenes/27c3a319dc7011e9bc200242ac110004" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImY0YzUzZGU3ZjJkZDRlNDk5YWZlNzgyMTRhZTI0MWEyIiwic2VjcmV0IjoiYjQ4NzcxMTIwNDQzNDFjY2EwNjE2NWIxZDM1Y2ZjNmUifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "27c3a319dc7011e9bc200242ac110004",
    "code": "0001",
    "name": "验证码登录",
    "remark": null,
    "creator": "系统",
    "creatorId": "00000000000000000000000000000000",
    "createdTime": "2019-09-29 17:46:39"
  },
  "option": null
}
```

[回目录](#目录)

### 新增消息场景

新建一个消息场景，编码不可重复。

请求方法：**POST**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|code|是|场景编号|
|String|name|是|场景名称|
|String|remark|否|备注|

请求参数示例：

```json
{
  "code": "0001",
  "name": "验证码登录"
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "b88e3c0ffa59420689d146ef705cb76b",
  "option": null
}
```

[回目录](#目录)

### 编辑消息场景

修改指定ID的消息场景编码、名称和备注。编码不可重复

请求方法：**PUT**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|场景ID|
|String|code|是|场景编号|
|String|name|是|场景名称|
|String|remark|否|备注|

请求参数示例：

```json
{
  "id": "b88e3c0ffa59420689d146ef705cb76b",
  "code": "0001",
  "name": "验证码登录",
  "remark": "aaa"
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 删除消息场景

删除指定ID的消息场景，仅限无配置消息模板参数的场景。如场景已配置模板，需先删除配置。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息场景ID|

请求参数示例：

```json
"b88e3c0ffa59420689d146ef705cb76b"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## 场景配置管理

### 获取场景配置列表

根据关键词(可选)分页查询指定场景ID的配置列表，关键词精确匹配场景编码|合作伙伴编码，模糊匹配场景名称|应用名称|合作伙伴名称。

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes/{id}/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|消息场景ID|
|String|keyword|否|查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|sceneId|场景ID|
|String|scene|场景名称|
|String|appId|应用ID|
|String|appName|应用名称|
|String|partnerCode|合作伙伴编码|
|String|partner|合作伙伴名称|
|String|templateId|模板ID|
|String|template|模板名称|
|String|sign|签名|

请求参数示例：

```sh
curl "http://192.168.236.8:6200/base/message/v1.0/scenes/27c3a319dc7011e9bc200242ac110004/configs?keyword=0001" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImE4N2NiNjFlNmU2YTRhNDNiODY1NmExMWViM2E0YTQ2Iiwic2VjcmV0IjoiMTA2YmIxY2FhNWJjNDc4MTk0MTdmNDkwYTI4NWE1YjAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "d172f49ff24311e99eca0242ac110003",
      "sceneId": "27c3a319dc7011e9bc200242ac110004",
      "scene": "0001-验证码登录",
      "appId": null,
      "appName": null,
      "partnerCode": null,
      "partner": null,
      "templateId": "387e156ddc7211e9bc200242ac110004",
      "template": "0001-验证码登录",
      "sign": "Insight"
    }
  ],
  "option": 1
}
```

[回目录](#目录)

### 添加场景配置

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/message/v1.0/scenes/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|sceneId|是|场景ID|
|String|appId|否|应用ID|
|String|appName|否|应用名称|
|String|partnerCode|否|合作伙伴编码|
|String|partner|否|合作伙伴名称|
|String|templateId|是|模板ID|
|String|sign|否|签名|

请求参数示例：

```json
{
  "sceneId": "1b68365b879147818107707699b10134",
  "templateId": "93cb8cf5870c4261a067f898baf07276"
}
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "1d6bf020a31b4ff2b6f62f1a9300e87f",
  "option": null
}
```

[回目录](#目录)

### 移除场景配置

删除指定ID的业务场景配置。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/scenes/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|消息场景配置ID|

请求参数示例：

```json
"1d6bf020a31b4ff2b6f62f1a9300e87f"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## DTO类型说明

### Reply

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|Boolean|success|接口调用是否成功，成功：true；失败：false|
|Integer|code|错误代码，2xx代表成功，4xx或5xx代表失败|
|String|message|错误消息，描述了接口调用失败原因|
|Object|data|接口返回数据|
|Object|option|附加数据，如分页数据的总条数|

[回目录](#目录)

### InsightMessage

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|id|UUID主键|
|String|messageId|消息ID|
|String|tenantId|租户ID|
|String|appId|应用ID|
|String|tag|消息标签|
|Integer|type|消息类型|
|List\<String>|receivers|接收人，用户ID(推送)/手机号(短信)/Email地址|
|String|title|消息标题|
|String|content|消息内容|
|Map\<String, Object>|params|推送参数|
|Date|expireDate|失效日期|
|Boolean|isBroadcast|是否广播消息|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

[回目录](#目录)

### ScheduleCall

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String|method|请求方法,GET/POST/PUT/DELETE|
|String|service|服务名/域名|
|String|url|URL|
|Map\<String, String>|headers|请求头数据|
|Object|body|请求体数据|

[回目录](#目录)
