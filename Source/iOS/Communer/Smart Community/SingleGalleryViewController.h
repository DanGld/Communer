//
//  SingleGalleryViewController.h
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BDDynamicGridViewController.h"


@interface SingleGalleryViewController : BDDynamicGridViewController<BDDynamicGridViewDelegate>{
    NSMutableArray * _items;
}

@property (strong, nonatomic) NSArray* images;
@property (strong, nonatomic) NSString* galleryTitle;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
- (IBAction)backBtn;

@end
