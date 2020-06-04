package com.insight.common.message.template;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insight.common.message.common.client.LogClient;
import com.insight.common.message.common.client.LogServiceClient;
import com.insight.common.message.common.dto.TemplateListDto;
import com.insight.common.message.common.entity.Template;
import com.insight.common.message.common.mapper.TemplateMapper;
import com.insight.utils.Generator;
import com.insight.utils.ReplyHelper;
import com.insight.utils.Util;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.OperateType;
import com.insight.utils.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息管理服务
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    private static final String BUSINESS = "消息模板管理";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LogServiceClient client;
    private final TemplateMapper mapper;

    /**
     * 构造方法
     *
     * @param client LogServiceClient
     * @param mapper TemplateMapper
     */
    public TemplateServiceImpl(LogServiceClient client, TemplateMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * 获取短信模板列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getTemplates(String tenantId, String keyword, int page, int size) {
        PageHelper.startPage(page, size);
        List<TemplateListDto> templates = mapper.getTemplates(tenantId, keyword);
        PageInfo<TemplateListDto> pageInfo = new PageInfo<>(templates);

        return ReplyHelper.success(templates, pageInfo.getTotal());
    }

    /**
     * 获取短信模板
     *
     * @param id 短信模板ID
     * @return Reply
     */
    @Override
    public Reply getTemplate(String id) {
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        return ReplyHelper.success(template);
    }

    /**
     * 新增短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @Override
    public Reply newTemplate(LoginInfo info, Template dto) {
        String id = Util.uuid();
        String tenantId = info.getTenantId();
        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setCode(newCode(tenantId));
        dto.setCreator(info.getUserName());
        dto.setCreatorId(info.getUserId());
        dto.setCreatedTime(LocalDateTime.now());

        mapper.addTemplate(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.INSERT, id, dto);

        return ReplyHelper.created(id);
    }

    /**
     * 编辑短信模板
     *
     * @param info 用户关键信息
     * @param dto  短信模板DTO
     * @return Reply
     */
    @Override
    public Reply editTemplate(LoginInfo info, Template dto) {
        String id = dto.getId();
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.editTemplate(dto);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, dto);

        return ReplyHelper.success();
    }

    /**
     * 删除短信模板
     *
     * @param info 用户关键信息
     * @param id   短信模板ID
     * @return Reply
     */
    @Override
    public Reply deleteTemplate(LoginInfo info, String id) {
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        int count = mapper.getConfigCount(id);
        if (count > 0) {
            return ReplyHelper.fail("该模板已配置到消息场景,请先删除相应配置");
        }

        mapper.deleteTemplate(id);
        LogClient.writeLog(info, BUSINESS, OperateType.DELETE, id, template);

        return ReplyHelper.success();
    }

    /**
     * 改变短信模板禁用/启用状态
     *
     * @param info   用户关键信息
     * @param id     短信模板ID
     * @param status 禁用/启用状态
     * @return Reply
     */
    @Override
    public Reply changeTemplateStatus(LoginInfo info, String id, boolean status) {
        Template template = mapper.getTemplate(id);
        if (template == null) {
            return ReplyHelper.fail("ID不存在,未更新数据");
        }

        mapper.changeTemplateStatus(id, status);
        LogClient.writeLog(info, BUSINESS, OperateType.UPDATE, id, template);

        return ReplyHelper.success();
    }

    /**
     * 获取日志列表
     *
     * @param tenantId 租户ID
     * @param keyword  查询关键词
     * @param page     分页页码
     * @param size     每页记录数
     * @return Reply
     */
    @Override
    public Reply getTemplateLogs(String tenantId, String keyword, int page, int size) {
        return client.getLogs(BUSINESS, keyword, page, size);
    }

    /**
     * 获取日志详情
     *
     * @param id 日志ID
     * @return Reply
     */
    @Override
    public Reply getTemplateLog(String id) {
        return client.getLog(id);
    }


    /**
     * 获取消息模板编码
     *
     * @param tenantId 租户ID
     * @return 消息模板编码
     */
    private String newCode(String tenantId) {
        String group = "Template" + (tenantId == null ? "" : ":" + tenantId);
        while (true) {
            String code = Generator.newCode("#4", group, false);
            int count = mapper.getTemplateCount(tenantId, code);
            if (count > 0) {
                continue;
            }

            return code;
        }
    }
}
