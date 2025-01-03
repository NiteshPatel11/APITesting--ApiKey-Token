package GoRest;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

public class GoRestNavigationTest {

    @Test
    public static void main(String[]args){
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            page.navigate("https://gorest.co.in/");


            System.out.println("Page Title: " + page.title());
            System.out.println("Navigate manually to get the token from the website.");

        }
    }
}
