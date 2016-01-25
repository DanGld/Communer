//
//  TutorialViewController.h
//  Smart Community
//
//  Created by Altair on 1/25/16.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ICETutorialController.h"

@interface TutorialViewController : UIViewController<ICETutorialControllerDelegate>
{
}
@property (strong, nonatomic) ICETutorialController *tutorialController;
@end
