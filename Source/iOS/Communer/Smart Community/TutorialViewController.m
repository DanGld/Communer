//
//  TutorialViewController.m
//  Smart Community
//
//  Created by Altair on 1/25/16.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import "TutorialViewController.h"
#import "SignInViewController.h"

@interface TutorialViewController ()
{
    BOOL bLastPageState;
}
@end

@implementation TutorialViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    ICETutorialPage *layer1 = [[ICETutorialPage alloc] initWithTitle:@"Connect to your community"
                                                            subTitle:@"your local organization, community center, other or all of them..."
                                                         pictureName:@"tutorial_01@2x.png"
                                                            duration:3.0];
    ICETutorialPage *layer2 = [[ICETutorialPage alloc] initWithTitle:@"Always up to date"
                                                            subTitle:@"Keep track on events, times, announcements and much more."
                                                         pictureName:@"tutorial_02@2x.png"
                                                            duration:3.0];
    ICETutorialPage *layer3 = [[ICETutorialPage alloc] initWithTitle:@"Engage your community"
                                                            subTitle:@"Take part in discussions, ask questions, get tickets to events and share with friends."
                                                         pictureName:@"tutorial_03@2x.png"
                                                            duration:3.0];
    ICETutorialPage *layer4 = [[ICETutorialPage alloc] initWithTitle:@"Discover new horizons"
                                                            subTitle:@"Join multiple communities, find activities, people, places and services around the world."
                                                         pictureName:@"tutorial_04@2x.png"
                                                            duration:3.0];
    NSArray *tutorialLayers = @[layer1,layer2,layer3,layer4];
    
    CGFloat titleFontSize, subtitleFontSize;
    NSUInteger titleOffset, subtitleOffset;
    titleFontSize = 28.f;
    subtitleFontSize = 22.f;
    titleOffset = 390;
    subtitleOffset = 330;
    //Use storyboard
    if ([[UIScreen mainScreen] respondsToSelector:@selector(scale)]) {
        if ([[UIScreen mainScreen] scale] == 2.0) {
            
            if([UIScreen mainScreen].bounds.size.height == 667){
                // iPhone retina-4.7 inch(iPhone 6)
                titleFontSize = 26.f;
                subtitleFontSize = 20.f;
                titleOffset = 360;
                subtitleOffset = 300;
            }
            else if([UIScreen mainScreen].bounds.size.height == 568){
                // iPhone retina-4 inch(iPhone 5 or 5s)
                titleFontSize = 22.f;
                subtitleFontSize = 18.f;
                titleOffset = 320;
                subtitleOffset = 260;
            }
            else{
                // iPhone retina-3.5 inch(iPhone 4s)
                titleFontSize = 22.f;
                subtitleFontSize = 18.f;
                titleOffset = 280;
                subtitleOffset = 220;
            }
        }
        else if ([[UIScreen mainScreen] scale] == 3.0)
        {
            //if you want to detect the iPhone 6+ only
            if([UIScreen mainScreen].bounds.size.height == 736.0){
                //iPhone retina-5.5 inch screen(iPhone 6 plus)
                titleFontSize = 28.f;
                subtitleFontSize = 22.f;
                titleOffset = 390;
                subtitleOffset = 330;
            }
            //iPhone retina-5.5 inch screen(iPhone 6 plus)
        }
    }

    // Set the common style for the title.
    ICETutorialLabelStyle *titleStyle = [[ICETutorialLabelStyle alloc] init];
    [titleStyle setFont:[UIFont fontWithName:@"Lato-Semibold" size:titleFontSize]];
    [titleStyle setTextColor:[UIColor whiteColor]];
    [titleStyle setLinesNumber:1];
    [titleStyle setOffset:titleOffset];
    [[ICETutorialStyle sharedInstance] setTitleStyle:titleStyle];
    
    // Set the subTitles style with few properties and let the others by default.
    ICETutorialLabelStyle *subtitleStyle = [[ICETutorialLabelStyle alloc] init];
    [subtitleStyle setFont:[UIFont fontWithName:@"Lato-Regular" size:subtitleFontSize]];
    [subtitleStyle setTextColor:[UIColor whiteColor]];
    [subtitleStyle setLinesNumber:0];
    [subtitleStyle setOffset:subtitleOffset];
    [[ICETutorialStyle sharedInstance] setSubTitleStyle:subtitleStyle];
    
    self.tutorialController = [[ICETutorialController alloc] initWithPages:tutorialLayers
                                                              delegate:self];

    // Init tutorial.
    [self.tutorialController startScrolling];
    [self.view addSubview:self.tutorialController.view];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    bLastPageState = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - ICETutorialController delegate
- (void)tutorialController:(ICETutorialController *)tutorialController scrollingFromPageIndex:(NSUInteger)fromIndex toPageIndex:(NSUInteger)toIndex {
    NSLog(@"Scrolling from page %lu to page %lu.", (unsigned long)fromIndex, (unsigned long)toIndex);
    // Auto Animation
    if(toIndex < 3){
        bLastPageState = NO;
    }
}

- (void)tutorialControllerDidReachLastPage:(ICETutorialController *)tutorialController {
    NSLog(@"Tutorial reached the last page.");
    if(!bLastPageState){
        bLastPageState = YES;
    }else{
        bLastPageState = NO;
        [self.tutorialController stopScrolling];
        SignInViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewController"];
        [self.navigationController pushViewController:controller animated:YES];
    }
}

- (void)tutorialController:(ICETutorialController *)tutorialController didClickOnLeftButton:(UIButton *)sender {
    NSLog(@"Button 1 pressed.");
}

- (void)tutorialController:(ICETutorialController *)tutorialController didClickOnRightButton:(UIButton *)sender {
    NSLog(@"Button 2 pressed.");
    NSLog(@"Auto-scrolling stopped.");
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
