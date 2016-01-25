//
//  FirstViewController.m
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "FirstViewController.h"
#import "SignInViewController.h"
#import "Networking.h"

@interface FirstViewController ()

@end

@implementation FirstViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    if ([[NSUserDefaults standardUserDefaults]
         objectForKey:@"jsonData"]) {
        
        [[Networking sharedInstance] fetchNewData];
        
    UITabbarControllerViewController *tabbar = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"main.tabbar"];
    
    SideMenuViewController *sideMenu = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"main.SideMenu"];
    
    MFSideMenuContainerViewController *container = [MFSideMenuContainerViewController
                                                    containerWithCenterViewController:tabbar
                                                    leftMenuViewController:sideMenu
                                                    rightMenuViewController:nil];
    [self presentViewController:container animated:YES completion:nil];
    }
    else {
        SignInViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewController"];
        [self.navigationController pushViewController:controller animated:NO];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
