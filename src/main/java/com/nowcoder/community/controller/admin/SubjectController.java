package com.nowcoder.community.controller.admin;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.Subject;
import com.nowcoder.community.service.CategoryService;
import com.nowcoder.community.service.SubjectService;
import com.nowcoder.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class SubjectController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubjectService subjectService;

    /**
     * 科目列表分页
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/subjectList")
    public String getSubjectList(Model model, Page page){
        page.setPath("/admin/subjectList");
        page.setRows(subjectService.findRows());
        List<Subject> subjects = subjectService.selectAll(page.getOffset(), page.getLimit());
        model.addAttribute("subjects", subjects);
        return "/site/admin/subject";
    }

    /**
     * 接收表单数据，添加科目
     * @param name
     * @return
     */
    @PostMapping("/addSubject")
    public String addSubject(@RequestParam("name") String name){
        //查询科目是否存在
        Subject subject = subjectService.selectByName(name);

        Subject subject1 = new Subject();
        if(subject == null){
            //不存在，可以添加科目
            subject1.setName(name);
            //插入数据
            subjectService.insertSubject(subject1);
        } else {
            System.out.println("科目" + name + "已存在！");
        }
        return "redirect:/admin/subjectList";
    }

    /**
     * 获取某id的科目信息，在编辑框中回显
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/getSubject")
    public String getSubject(Integer id){
        Subject subject = subjectService.selectById(id);
        if(subject != null){
            Map<String, Object> map = new HashMap<>();
            //存入科目id和科目名称
            map.put("name", subject.getName());
            map.put("id", subject.getId());
            return CommunityUtil.getJSONString(0, null, map);
        }else {
            return CommunityUtil.getJSONString(403,"查询科目信息失败！");
        }
    }

    /**
     * 编辑科目信息
     * @param subject
     * @return
     */
    @PostMapping("/updateSubject")
    public String updateSubject(Subject subject){
        if(subjectService.selectByName(subject.getName()) == null){
            //科目名称不存在，可以保存编辑
            subjectService.updateSubject(subject);
        }else {
            //科目名称存在
            System.out.println("科目" + subject.getName() + "已存在！");
        }
        return "redirect:/admin/subjectList";
    }

    /**
     * 删除单个科目
     * @param id
     * @return
     */
    @PostMapping("/delSubject")
    @ResponseBody
    public String deleteSubject(Integer id){
        //查询是否有分类关联此科目
        int count = categoryService.countBySubjectId(id);
        if(count > 0){
            return CommunityUtil.getJSONString(403, "存在分类绑定该科目信息，删除失败！");
        }
        subjectService.deleteById(id);
        //删除成功
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 批量删除科目
     * @param ids
     * @return
     */
    @ResponseBody
    @PostMapping("/batchDelSub")
    public String batchDelSubject(@RequestParam("ids[]") Integer[] ids){
        int length = ids.length;
        //传入的参数有数据时，才进行批量删除操作
        if(length > 0){
            //根据ids进行批量查询分类，若其中有科目与分类绑定，则不能进行批量删除
            int count = categoryService.batchCountBySubId(ids);
            if(count > 0){
                return CommunityUtil.getJSONString(403, "存在分类绑定科目，批量删除失败！");
            }
            subjectService.batchDel(ids);
            return CommunityUtil.getJSONString(0);
        }
        return CommunityUtil.getJSONString(403, "参数异常，批量删除失败！");
    }
}
