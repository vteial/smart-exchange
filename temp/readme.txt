do delete this file

   jwplayer("vplayer").setup({
     sources: [{file:"http://cdn32.vidmad.net/h7tod2tsamlbu3tf6rutlovv3d755mjwdavsczfm37o4mlzkgpi5575pp4iq/v.mp4",label:"720p"},{file:"http://cdn32.vidmad.net/h7tod2tsamlbu3tf6rutlovv3d755mjwdavsczfm3ho4mlzkgpit2w3evcpq/v.mp4",label:"360p","default": "true"},{file:"http://cdn32.vidmad.net/h7tod2tsamlbu3tf6rutlovv3d755mjwdavsczfm3po4mlzkgpiqizlwz3va/v.mp4",label:"240p"}],
     image: "http://cdn32.vidmad.net/i/01/00000/y0dkgjut9d4u.jpg",
     duration:"7942",
     width: "100%",
     height: "350",
     aspectratio: "16:9",
     preload: "none",
     androidhls: "true",
     startparam: "start"

     ,tracks: []
     ,skin: "glow",abouttext:"VidMAD", aboutlink:"http://vidmad.tv"
   });
 var vvplay,vvad;
 jwplayer().onTime(function(x) {
     if(5>0 && x.position>=5 && vvad!=1){vvad=1;$('div.video_ad_fadein').fadeIn('slow');}
 });
 jwplayer().onPlay(function(x) { doPlay(x); });
 jwplayer().onComplete(function() { $('div.video_ad').show(); });
 function doPlay(x)
 {
   $('div.video_ad').hide();
   if(vvplay)return;
   vvplay=1;
   $.get('http://vidmad.tv/dl?op=view&file_code=y0dkgjut9d4u&hash=3512-45-121-1478185093-1a0333636f1d6957789ed7e52313fd6d', function(data) {$('#fviews').html(data);} );
 }




