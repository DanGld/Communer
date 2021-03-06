//
//  BDViewController+Private.m
//  BDDynamicGridViewDemo
//
//  Created by Nor Oh on 6/21/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "BDViewController+Private.h"
#define kNumberOfPhotos 25
@implementation SingleGalleryViewController (Private)

-(void)buildBarButtons
{
    UIBarButtonItem * reloadButton = [[UIBarButtonItem alloc] initWithTitle:@"Lay it!"
                                                                      style:UIBarButtonItemStylePlain
                                                                     target:self 
                                                                     action:@selector(animateReload)];

    
    self.navigationItem.rightBarButtonItems = [NSArray arrayWithObjects: reloadButton, nil];

}

-(NSArray*)_imagesFromBundle
{   
    NSArray *images = [NSArray array];
    NSBundle *bundle = [NSBundle mainBundle];
    for (int i=0; i< kNumberOfPhotos; i++) {
        NSString *path = [bundle pathForResource:[NSString stringWithFormat:@"%d", i + 1] ofType:@"jpg"];
        UIImage *image = [UIImage imageWithContentsOfFile:path];
        if (image) {
            images = [images arrayByAddingObject:image];
        }
    }
    return images;
}


- (void)_demoAsyncDataLoading
{
    [self reloadData];
    NSMutableArray *images = [NSMutableArray array];
    for (int i = 0; i < _items.count; i++) {
        UIImageView *imageView = [_items objectAtIndex:i];
        UIImage *image = imageView.image;
        [images addObject:image];
        imageView.frame = CGRectMake(0, 0, image.size.width, image.size.height);
        
        [self performSelector:@selector(animateUpdate:) 
                   withObject:[NSArray arrayWithObjects:imageView, image, nil]
                   afterDelay:0];
    }
}

- (void) animateUpdate:(NSArray*)objects
{
    UIImageView *imageView = [objects objectAtIndex:0];
    UIImage* image = [objects objectAtIndex:1];
    [UIView animateWithDuration:0.5 
                     animations:^{
                         imageView.alpha = 0.f;
                     } completion:^(BOOL finished) {
                         imageView.image = image;
                         [UIView animateWithDuration:0.5
                                          animations:^{
                                              imageView.alpha = 1;
                                          } completion:^(BOOL finished) {
                                              NSArray *visibleRowInfos =  [self visibleRowInfos];
                                              for (BDRowInfo *rowInfo in visibleRowInfos) {
                                                  [self updateLayoutWithRow:rowInfo animiated:YES];
                                              }
                                          }];
                     }];
}

@end
