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
  - [发送送标准消息](#发送送标准消息)
  - [发送自定义消息](#发送自定义消息)
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
  - [禁用消息场景](#禁用消息场景)
  - [启用消息场景](#启用消息场景)
- [场景配置管理](#场景配置管理)
  - [获取场景配置列表](#获取场景配置列表)
  - [添加场景配置](#添加场景配置)
  - [移除场景配置](#移除场景配置)
- [DTO类型说明](#DTO类型说明)

## 概述

Insight 消息中心提供消息发送和计划任务调度的能力。用户可以根据需求，发送标准化的或者自定义的本地消息、通知和短信。计划任务功能则作为异步调用的补偿机制存在，为意外失败的调用提供自动重试机制。消息中心同时也提供短信验证码的生成和验证功能。
>注：通知和短信的发送渠道需要自行对接。

### 主要功能

1. 短信验证码的生成和验证
2. 根据消息场景自动适配消息模板发送消息(本地消息|通知|短信)
3. 发送自定义消息(本地消息|通知|短信)
4. 计划任务管理
5. 消息模板管理
6. 消息场景管理
7. 消息场景配置管理，为场景配置不同的模板和参数

### 通讯方式

接口支持 **HTTP/HTTPS** 协议的 **GET/POST/PUT/DELETE** 方法，支持 **URL Params** 、 **Path Variable** 或 **BODY** 传递接口参数。如使用 **BODY** 传参，则需使用 **Json** 格式的请求参数。接口 **/URL** 区分大小写，请求以及返回都使用 **UTF-8** 字符集进行编码，接口返回的数据封装为统一的 **Json** 格式。

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
|String|appId|是|应用ID|
|Integer|type|是|验证码类型:0.验证手机号;1.注册用户账号;2.重置密码;3.修改支付密码;4.修改手机号|
|String|mobile|是|手机号|
|Integer|length|是|验证码长度,建议4-8位|
|Integer|minutes|是|验证码有效时间(分钟)|

请求参数示例：

```json
{
  "appId": "9dd99dd9e6df467a8207d05ea5581125",
  "type": 0,
  "mobile": "13958085903",
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

```shell
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

### 发送送标准消息

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/message/v1.0/messages**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**method**

接口URL：**/base/message**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

```shell
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

```shell
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
|\<T>|content|是|任务内容,泛型|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|String||任务ID|

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
|String||是|任务ID|

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
|String||是|任务ID|

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
|String||是|任务ID|

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
|String||是|任务ID|

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

根据关键词(可选)分页查询租户的消息模板，关键词可精确匹配模板编号或模糊匹配模板标题。

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

```shell
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
|String||是|消息模板ID|

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
  "tag": null,
  "type": 4,
  "title": "验证码登录",
  "expire": null,
  "creator": "系统",
  "createdTime": "2019-09-29 17:46:39"
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

### 新增消息模板

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 编辑消息模板

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**PUT**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

删除指定ID的消息模板。

请求方法：**DELETE**

接口URL：**/base/message/v1.0/templates**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String||是|消息模板ID|

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

### 禁用消息模板

禁用指定ID的消息模板。

请求方法：**PUT**

接口URL：**/base/message/v1.0/templates/disable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|String||是|消息模板ID|

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
|String||是|消息模板ID|

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

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 获取消息场景详情

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes/{id}**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 新增消息场景

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 编辑消息场景

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**PUT**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**DELETE**

接口URL：**/base/message/v1.0/scenes**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 禁用消息场景

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**PUT**

接口URL：**/base/message/v1.0/scenes/disable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 启用消息场景

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**PUT**

接口URL：**/base/message/v1.0/scenes/enable**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

## 场景配置管理

### 获取场景配置列表

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**GET**

接口URL：**/base/message/v1.0/scenes/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 添加场景配置

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/message/v1.0/scenes/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### 移除场景配置

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**DELETE**

接口URL：**/base/message/v1.0/scenes/configs**

请求参数如下：

|类型|字段|是否必需|字段说明|
| ------------ | ------------ | ------------ | ------------ |
|||||

接口返回数据类型：

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
||||

请求参数示例：

```json
{
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

### Schedule

|类型|字段|字段说明|
| ------------ | ------------ | ------------ |
|Boolean|success|接口调用是否成功，成功：true；失败：false|
|Integer|code|错误代码，2xx代表成功，4xx或5xx代表失败|
|String|message|错误消息，描述了接口调用失败原因|
|Object|data|接口返回数据|
|Object|option|附加数据，如分页数据的总条数|

[回目录](#目录)
