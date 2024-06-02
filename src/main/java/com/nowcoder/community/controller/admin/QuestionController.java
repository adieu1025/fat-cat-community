package com.nowcoder.community.controller.admin;

import com.nowcoder.community.controller.UserController;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.*;
import com.nowcoder.community.utils.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class QuestionController {

    //选择题
    @Autowired
    private ChoiceQuestionService choiceQuestionService;

    //判断题
    @Autowired
    private TfQuestionService tfQuestionService;

    //主观题
    @Autowired
    private SubjectiveQuestionService subjectiveQuestionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private PaperQuestionService paperQuestionService;

    // 问题图片的存放路径
    @Value("${community.path.questionImg}")
    private String questionImgPath;

    // http://localhost:8080
    @Value("${community.path.domain}")
    private String domain;

    // /community
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    /**
     * 跳转到题库管理页面
     *
     * @return
     */
    @GetMapping("/questionManager")
    public String getQuestionPage() {
        return "/site/admin/question-manager";
    }

    /**
     * 分页查询单选题,列表展示
     *
     * @return
     */
    @GetMapping("/singleList")
    public String getSingleList(Model model, Page page) {
        //分页信息
        page.setPath("/admin/singleList");
        page.setRows(choiceQuestionService.selectSingleRows());

        List<ChoiceQuestion> singleList = choiceQuestionService.selectAll(page.getOffset(), page.getLimit());
        List<Map<String, Object>> singleDto = new ArrayList<>();

        if (singleList != null) {
            for (ChoiceQuestion single : singleList) {
                Map<String, Object> map = new HashMap<>();
                //存入单选题本身
                map.put("single", single);
                //存入单选题科目、分类
                int categoryId = single.getCategoryId();
                Category category = categoryService.selectById(categoryId);
                if (category != null) {
                    String categoryName = category.getName();
                    map.put("categoryName", categoryName);
                    int subjectId = category.getSubjectId();
                    Subject subject = subjectService.selectById(subjectId);
                    if (subject != null) {
                        String subjectName = subject.getName();
                        map.put("subjectName", subjectName);
                    }
                }
                //全部信息存入singleDto
                singleDto.add(map);
            }
        }

        model.addAttribute("singleDto", singleDto);
        return "/site/admin/single";
    }

    /**
     * 获取所有分类，显示在添加/编辑框的下拉列表中
     *
     * @return
     */
    @PostMapping("/findCategories")
    @ResponseBody
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    /**
     * 上传题目图片并进行回显（传回图片访问路径）
     *
     * @param img
     * @return
     */
    @ResponseBody
    @PostMapping("/uploadQuesImg")
    public String uploadImg(@RequestParam("file") MultipartFile img) {
        //判断接收到的图片是否为空
        if (img == null) {
            return CommunityUtil.getJSONString(403, "您还没有选择图片哦");
        }
        //判断图片格式是否错误
        String filename = img.getOriginalFilename();
        String suffix = "";
        if (filename != null) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        if (StringUtils.isBlank(suffix)) {
            return CommunityUtil.getJSONString(403, "图片格式错误！");
        }

        //随机生成文件名
        filename = CommunityUtil.createUUID() + suffix;
        //确定文件存放路径
        File dest = new File(questionImgPath + "/" + filename);
        //存储文件
        try {
            img.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("上传图片失败,服务器发生异常!", e);
        }

        //传回当前问题的图片访问路径（web路径），方便前端进行图片的回显
        Map<String, Object> map = new HashMap<>();
        String url = domain + contextPath + "/admin/quesImg/" + filename;
        map.put("url", url);
        return CommunityUtil.getJSONString(0, null, map);
    }

    /**
     * 根据问题图片路径，在前端进行回显，（web路径映射到磁盘路径）
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/quesImg/{fileName}")
    public void showQuesImg(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //文件存放位置
        fileName = questionImgPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                OutputStream outputStream = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取问题图片失败：" + e.getMessage());
        }
    }

    /**
     * 添加选择题
     *
     * @param single
     * @return
     */
    @PostMapping("/addSingle")
    public String addSingle(ChoiceQuestion single) {
        //进行判断，各项属性都符合后，存入数据库
        if (choiceQuestionService.checkSingle(single)) {
            //检查无误后，可以调用service进行存储
            choiceQuestionService.insertOne(single);
        } else {
            System.out.println("保存题目信息失败！" + single.toString());
        }
        return "redirect:/admin/singleList";
    }

    /**
     * 题目详情（详细展示题目所有信息，包括图片、解析等）
     *
     * @param singleId
     * @param model
     * @return
     */
    @GetMapping("/singleDetail/{singleId}")
    public String singleDetail(@PathVariable("singleId") Integer singleId, Model model) {
        //查询题目
        ChoiceQuestion single = choiceQuestionService.selectById(singleId);
        if (single != null) {
            model.addAttribute("single", single);
            //查询分类名称
            model.addAttribute("categoryName", categoryService.selectById(single.getCategoryId()).getName());
        }
        return "/site/admin/single-detail";
    }

    /**
     * 根据id查询问题，回显在编辑框中
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/getSingle")
    public String getSingle(Integer id) {
        ChoiceQuestion single = choiceQuestionService.selectById(id);
        if (single != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("single", single);
            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(403, "查询问题信息失败！");
        }
    }

    /**
     * 编辑选择题
     *
     * @param single
     * @return
     */
    @PostMapping("/updateSingle")
    public String updateSingle(ChoiceQuestion single) {
        if (choiceQuestionService.checkSingle(single)) {
            choiceQuestionService.updateSingle(single);
        } else {
            System.out.println("编辑题目信息失败！" + single.toString());
        }
        return "redirect:/admin/singleList";
    }

    /**
     * 根据id删除题目
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/delSingle")
    public String delOne(Integer id) {
        //查询该判断题是否关联试卷
        if (id != null) {
            int count = paperQuestionService.countByQuesId(id);
            if (count > 0) {
                return CommunityUtil.getJSONString(403, "存在试卷关联该题目信息，删除失败！");
            } else {
                choiceQuestionService.delById(id);
            }
        }
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 根据id集合批量删除问题
     *
     * @param ids
     * @return
     */
    @ResponseBody
    @PostMapping("/batchDelSingle")
    public String batchDel(@RequestParam("ids[]") Integer[] ids) {
        if (ids.length > 0) {
            int count = paperQuestionService.batchCountByQuesId(ids);
            if (count > 0) {
                return CommunityUtil.getJSONString(403, "存在试卷绑定问题，批量删除失败！");
            }
            //进行批量删除
            choiceQuestionService.batchDel(ids);
            return CommunityUtil.getJSONString(0);
        }
        return CommunityUtil.getJSONString(403, "参数异常，批量删除失败！");
    }

    /**
     * 判断题管理列表
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/tfQuesList")
    public String tfQuesList(Model model, Page page) {
        page.setPath("/admin/tfQuesList");
        page.setRows(tfQuestionService.selectRows());
        //分页查询
        List<TfQuestion> tfQuestionList = tfQuestionService.selectPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> tfQuestionDto = new ArrayList<>();

        if (tfQuestionList != null) {
            for (TfQuestion tfQuestion : tfQuestionList) {
                Map<String, Object> map = new HashMap<>();
                //存入题目本身
                map.put("tfQuestion", tfQuestion);
                //存入分类、科目信息
                Category category = categoryService.selectById(tfQuestion.getCategoryId());
                if (category != null) {
                    String categoryName = category.getName();
                    map.put("categoryName", categoryName);
                    int subjectId = category.getSubjectId();
                    Subject subject = subjectService.selectById(subjectId);
                    if (subject != null) {
                        String subjectName = subject.getName();
                        map.put("subjectName", subjectName);
                    }
                }
                tfQuestionDto.add(map);
            }
        }
        model.addAttribute("tfQuestionDto", tfQuestionDto);
        return "/site/admin/tf-question";
    }

    /**
     * 添加判断题
     *
     * @param tfQuestion
     * @return
     */
    @PostMapping("/addTf")
    public String addTf(TfQuestion tfQuestion) {
        if (tfQuestion != null) {
            tfQuestionService.insertOne(tfQuestion);
        } else {
            System.out.println("保存判断题题目信息失败！");
        }

        return "redirect:/admin/tfQuesList";
    }

    /**
     * 题目详情（判断题）
     *
     * @param tfId
     * @param model
     * @return
     */
    @GetMapping("/tfDetail/{tfId}")
    public String tfDetail(@PathVariable("tfId") Integer tfId, Model model) {
        //查询题目
        TfQuestion tf = tfQuestionService.selectById(tfId);
        if (tf != null) {
            model.addAttribute("tf", tf);
            //查询分类名称
            model.addAttribute("categoryName", categoryService.selectById(tf.getCategoryId()).getName());
        }
        return "/site/admin/tf-detail";
    }

    /**
     * 查询判断题信息，回显在编辑框中
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/getTf")
    public String getTf(Integer id) {
        TfQuestion tf = tfQuestionService.selectById(id);
        if (tf != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("tf", tf);
            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(403, "查询问题信息失败！");
        }
    }

    /**
     * 编辑判断题
     *
     * @param tf
     * @return
     */
    @PostMapping("/updateTf")
    public String updateTf(TfQuestion tf) {
        if (tf != null) {
            tfQuestionService.updateOne(tf);
        } else {
            System.out.println("更新判断题信息失败！");
        }
        return "redirect:/admin/tfQuesList";
    }

    /**
     * 根据id删除判断题
     *
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/delTf")
    public String delOneTf(Integer id) {
        //查询该题目是否关联试卷
        if (id != null) {
            int count = paperQuestionService.countByTfId(id);
            if (count > 0) {
                return CommunityUtil.getJSONString(403, "存在试卷关联该题目信息，删除失败！");
            } else {
                tfQuestionService.delById(id);
            }
        }
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 主观题（申论/面试）题目管理列表
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/subjectiveList")
    public String subjectiveList(Model model, Page page) {
        //查找主观题
        page.setPath("/admin/subjectiveList");
        page.setRows(subjectiveQuestionService.selectRows());

        //分页查询
        List<SubjectiveQuestion> subjectiveList = subjectiveQuestionService.selectPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> subjectiveDto = new ArrayList<>();

        if (subjectiveList != null) {
            for (SubjectiveQuestion subjective : subjectiveList) {
                Map<String, Object> map = new HashMap<>();
                //存入题目本身
                map.put("subjective", subjective);
                //存入分类、科目信息
                Category category = categoryService.selectById(subjective.getCategoryId());
                if (category != null) {
                    String categoryName = category.getName();
                    map.put("categoryName", categoryName);
                }
                Subject subject = subjectService.selectById(subjective.getSubjectId());
                if (subject != null) {
                    map.put("subjectName", subject.getName());
                }
                subjectiveDto.add(map);
            }
        }

        model.addAttribute("subjectiveDto", subjectiveDto);
        return "/site/admin/subjective-question";
    }

    /**
     * 添加一条主观题
     * @param subjectiveQuestion
     * @return
     */
    @PostMapping("/addSubjective")
    public String addSubjective(SubjectiveQuestion subjectiveQuestion) {
        if (subjectiveQuestion != null) {
            //补充科目id信息
            Category category = categoryService.selectById(subjectiveQuestion.getCategoryId());
            subjectiveQuestion.setSubjectId(category.getSubjectId());
            subjectiveQuestionService.insertOne(subjectiveQuestion);
        } else {
            System.out.println("保存主观题题目信息失败！");
        }

        return "redirect:/admin/subjectiveList";
    }

    /**
     * 题目详情（主观题）
     * @param subjectiveId
     * @param model
     * @return
     */
    @GetMapping("/subjectiveDetail/{subjectiveId}")
    public String subjectiveDetail(@PathVariable("subjectiveId") Integer subjectiveId, Model model) {
        SubjectiveQuestion subjectiveQuestion = subjectiveQuestionService.selectById(subjectiveId);
        if(subjectiveQuestion != null) {
            model.addAttribute("subjective", subjectiveQuestion);
            model.addAttribute("categoryName", categoryService.selectById(subjectiveQuestion.getCategoryId()).getName());
        }
        return "/site/admin/subjective-detail";
    }

    /**
     * 查询主观题信息，回显在编辑框中
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/getSubjective")
    public String getSubjective(Integer id) {
        SubjectiveQuestion subjective = subjectiveQuestionService.selectById(id);
        if (subjective != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("subjective", subjective);
            return CommunityUtil.getJSONString(0, null, map);
        } else {
            return CommunityUtil.getJSONString(403, "查询问题信息失败！");
        }
    }

    /**
     * 更新一条主观题信息
     * @param subjectiveQuestion
     * @return
     */
    @PostMapping("/updateSubjective")
    public String updateSubjective(SubjectiveQuestion subjectiveQuestion) {
        if(subjectiveQuestion != null) {
            //设置科目信息
            Category category = categoryService.selectById(subjectiveQuestion.getCategoryId());
            subjectiveQuestion.setSubjectId(category.getSubjectId());
            subjectiveQuestionService.updateOne(subjectiveQuestion);
        } else {
            System.out.println("更新主观题信息失败！");
        }

        return "redirect:/admin/subjectiveList";
    }

    /**
     * 根据id删除主观题
     * @param id
     * @return
     */
    @PostMapping("/delSubjective")
    @ResponseBody
    public String delSubjectiveById(Integer id) {
        if(id == null || id < 1) {
            return CommunityUtil.getJSONString(403, "参数错误！请重试");
        }
        subjectiveQuestionService.delOne(id);
        return CommunityUtil.getJSONString(0);
    }

}
