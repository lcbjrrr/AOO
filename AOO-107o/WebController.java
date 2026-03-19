@Controller
public class WebController {
   @GetMapping("/")
   public String home() {
       return "index"; 
   }
   @PostMapping("/greet")
   public String greet(@RequestParam("userName") String userName,
								Model model){
       model.addAttribute("name", userName); 
       return "hello";
   }
}
