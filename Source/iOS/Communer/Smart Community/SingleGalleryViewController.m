//
//  SingleGalleryViewController.m
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SingleGalleryViewController.h"
#import "BDViewController+Private.h"
#import "BDRowInfo.h"
#import "UIImageView+AFNetworking.h"
#import "LogicServices.h"

@interface SingleGalleryViewController ()

@end

@implementation SingleGalleryViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [_titleLabel setText:_galleryTitle];
    
    _items = [NSMutableArray array];
    for (ImageModel* image in _images) {

        UIImageView* imageView = [[UIImageView alloc]init];
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:image.link]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        UIImageView* imageVieww = [[UIImageView alloc]init];
        [imageView setImageWithURL:[NSURL URLWithString:[[LogicServices sharedInstance]getCurrentCommunity].communityImageUrl]];
        [imageView setImageWithURLRequest:imageRequest
                              placeholderImage:imageVieww.image
                                       success:nil
                                       failure:nil];

        
        imageView.frame = CGRectMake(0, 0, 44, 44);
        imageView.clipsToBounds = YES;
        [_items addObject:imageView];
        
    }
    if (floor(NSFoundationVersionNumber) <= NSFoundationVersionNumber_iOS_6_1) {
        self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    } else {
        self.navigationController.navigationBar.barStyle = UIBarStyleDefault;
    }
    
    
    self.delegate = self;
    
    self.onLongPress = ^(UIView* view, NSInteger viewIndex){
        NSLog(@"Long press on %@, at %d", view, viewIndex);
    };
    
    self.onDoubleTap = ^(UIView* view, NSInteger viewIndex){
        NSLog(@"Double tap on %@, at %d", view, viewIndex);
    };
    [self _demoAsyncDataLoading];
    [self buildBarButtons];
}

- (void)animateReload
{
    _items = [NSArray new];
    [self _demoAsyncDataLoading];
}

- (NSUInteger)numberOfViews
{
    return _items.count;
}

-(NSUInteger)maximumViewsPerCell
{
    return 5;
}

- (UIView *)viewAtIndex:(NSUInteger)index rowInfo:(BDRowInfo *)rowInfo
{
    UIImageView * imageView = [_items objectAtIndex:index];
    return imageView;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
    //Call super when overriding this method, in order to benefit from auto layout.
    [super shouldAutorotateToInterfaceOrientation:toInterfaceOrientation];
    return YES;
}

- (CGFloat)rowHeightForRowInfo:(BDRowInfo *)rowInfo
{
    //    if (rowInfo.viewsPerCell == 1) {
    //        return 125  + (arc4random() % 55);
    //    }else {
    //        return 100;
    //    }
    return 55 + (arc4random() % 125);
}

- (IBAction)backBtn {

    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
