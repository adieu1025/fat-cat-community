package com.nowcoder.community.controller.admin;

import com.nowcoder.community.entity.Category;
import com.nowcoder.community.entity.ChoiceQuestion;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.Subject;
import com.nowcoder.community.service.CategoryService;
import com.nowcoder.community.service.ChoiceQuestionService;
import com.nowcoder.community.service.SubjectService;
import com.nowcoder.community.utils.CommunityUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ChoiceQuestionService choiceQuestionService;

    /**
     * 列表展示分类
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/categoryList")
    public String getCategoryList(Model model, Page page) {
        page.setPath("/admin/categoryList");
        page.setRows(categoryService.findRows());
        List<Category> categories = categoryService.selectAll(page.getOffset(), page.getLimit());
        List<Map<String, Object>> categoryDto = new ArrayList<>();

        if (categories != null) {
            for (Category category : categories) {
                Map<String, Object> map = new HashMap<>();
                //存入分类本身
                map.put("category", category);
                //存入科目名
                Subject subject = subjectService.selectById(category.getSubjectId());
                if (subject != null) {
                    map.put("subjectName", subject.getName());
                }
                categoryDto.add(map);
            }
        }
        model.addAttribute("categoryDto", categoryDto);

        //传入科目信息，方便添加分类方框中回显科目列表
        List<Subject> subjects = subjectService.getAll();
        if (subjects.size() > 0) {
            model.addAttribute("subjects", subjects);
        }

        return "/site/admin/category";
    }

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PostMapping("/addCategory")
    public String addCategory(Category category) {
        //查询分类是否存在
        Category category1 = categoryService.selectByName(category.getName());
        if (category1 == null) {
            categoryService.insertCategory(category);
        } else {
            System.out.println("分类" + category.getName() + "已存在");
        }
        return "redirect:/admin/categoryList";
    }

    /**
     * 获取所有科目，显示在编辑框的下拉列表中
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/findAll")
    public List<Subject> findAll() {
        return subjectService.getAll();
    }

    /**
     * 获取某id的分类信息，在编辑框中回显
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/getCategory")
    public String getCategory(Integer id) {
        Category category = categoryService.selectById(id);
        if (category != null) {
            Map<String, Object> map = new HashMap<>();
            //存入分类id、分类名、所属科目
            map.put("name", category.getName());
            map.put("id", category.getId());
            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(403, "查询分类信息失败！");
        }
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @PostMapping("/updateCategory")
    public String updateCategory(Category category) {
        Category category1 = categoryService.selectByName(category.getName());
        if (category1 == null || category.getSubjectId() != category1.getSubjectId()) {
            //分类名称不存在 或者 所属科目不相同时，可以保存编辑
            categoryService.updateCategory(category);
        } else {
            System.out.println("分类" + category.getName() + "已存在！");
        }
        return "redirect:/admin/categoryList";
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/delCategory")
    public String delById(Integer id) {
        //查询是否有题目关联此分类
        int count = choiceQuestionService.countByCategoryId(id);
        if (count > 0) {
            return CommunityUtil.getJSONString(403, "存在题目绑定该分类信息，删除失败！");
        }
        categoryService.deleteById(id);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 批量删除分类
     *
     * @param ids
     * @return
     */
    @ResponseBody
    @PostMapping("/batchDelCategory")
    public String batchDel(@RequestParam("ids[]") Integer[] ids) {
        int length = ids.length;
        if (length > 0) {
            int count = choiceQuestionService.batchCountByCateId(ids);
            if (count > 0) {
                return CommunityUtil.getJSONString(403, "存在题目绑定分类，批量删除失败！");
            }
            //进行批量删除
            categoryService.batchDel(ids);
            return CommunityUtil.getJSONString(0);
        }

        return CommunityUtil.getJSONString(403, "参数异常，批量删除失败！");
    }

    /**
     * 查找主观题（科目为申论、面试）的所有分类，展示在添加主观题的分类列表中
     * @return
     */
    @PostMapping("/findSubjectiveCategories")
    @ResponseBody
    public List<Category> findSubjectiveCate() {
        return categoryService.findSubjectiveCate();
    }
}
