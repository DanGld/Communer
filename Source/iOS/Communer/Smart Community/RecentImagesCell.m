//
//  RecentImagesCell.m
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "RecentImagesCell.h"
#import "ImageCollectionCell.h"
#import "UIImageView+AFNetworking.h"
#import "LogicServices.h"


@implementation RecentImagesCell
NSArray* lastImages;

- (void)awakeFromNib {
    self.collectionView.delegate = self;
    self.collectionView.dataSource = self;
    [self.collectionView registerNib:[UINib nibWithNibName:@"ImageCollectionCell" bundle:[NSBundle mainBundle]]
        forCellWithReuseIdentifier:@"ImageCollectionCell"];
    lastImages = [[LogicServices sharedInstance] getLast10Images];
    NSString* obj ;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reloadData)
                                                 name:@"reloadData"
                                               object:obj];

}

-(void)reloadData {
    lastImages = [[LogicServices sharedInstance] getLast10Images];
    [_collectionView reloadData];
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return lastImages.count;
}			


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ImageCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"ImageCollectionCell" forIndexPath:indexPath];
    
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:lastImages[indexPath.row]]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [cell.imageView setImageWithURLRequest:imageRequest
                     placeholderImage:[UIImage imageNamed:@"placeHolder"]
                              success:nil
                              failure:nil];
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"openGalleryTab"
                                                        object:nil
                                                      userInfo:nil];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(125, 100);
}



@end
