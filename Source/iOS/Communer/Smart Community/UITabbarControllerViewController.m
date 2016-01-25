//
//  UITabbarControllerViewController.m
//  Smart Community
//
//  Created by oren shany on 7/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "UITabbarControllerViewController.h"

@interface UITabbarControllerViewController ()

@end

@implementation UITabbarControllerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[UITabBar appearance] setBackgroundColor:[UIColor colorWithRed:3.0f/255.0f green:169.0f/255.0f blue:244.0f/255.0f alpha:1]];
    [[UITabBar appearance] setTintColor:[UIColor whiteColor]];
    
    UITabBarItem *item = [self.tabBar.items objectAtIndex:0];
    item.image = [[UIImage imageNamed:@"home_icon_opacity"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    item = [self.tabBar.items objectAtIndex:1];
    item.image = [[UIImage imageNamed:@"calendar_icon_opacity"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    item = [self.tabBar.items objectAtIndex:2];
    item.image = [[UIImage imageNamed:@"messages_icon_opacity"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    item = [self.tabBar.items objectAtIndex:3];
    item.image = [[UIImage imageNamed:@"gallery_icon_opacity"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    item = [self.tabBar.items objectAtIndex:4];
    item.image = [[UIImage imageNamed:@"guide_icon_opacity"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
