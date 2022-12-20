@extends('pemilik.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Tambah Data Rumah Kos</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Beranda</a></li>
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos') }}">Data Rumah Kos</a></li>
              <li class="breadcrumb-item active">Tambah</li>
            </ol>
          </div>
        </div>
      </div><!-- /.container-fluid -->
    </section>
    <section class="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <!-- general form elements -->
            <div class="card card-primary">
              <div class="card-header">
                <h3 class="card-title">Form Data Rumah Kos</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'pemilik/rumah-kos-tambah','files' => true]) }}
                <div class="card-body">
                  <input type="hidden" name="user_pemilik_id" value="{{ Session::get('user_id') }}">
                  <div class="form-group">
                    {{ Form::label('nama', 'Nama Rumah Kos') }}  
                    {{ Form::text('nama',null,['class' => 'form-control']) }}
                    @if($errors->has('nama'))
                          <div class="text-danger">
                              {{ $errors->first('nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('alamat', 'Alamat') }}  
                    {{ Form::text('alamat',null,['class' => 'form-control']) }}
                    @if($errors->has('alamat'))
                          <div class="text-danger">
                              {{ $errors->first('alamat')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('kecamatan', 'Kecamatan') }}  
                    {{ Form::select('kecamatan', $kec, null,['class' => 'form-control select2','id' => 'kecamatan']) }}
                    @if($errors->has('kecamatan'))
                          <div class="text-danger">
                              {{ $errors->first('kecamatan')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('wilayah', 'Kelurahan') }}  
                    {{ Form::select('wilayah', [], null,['class' => 'form-control select2','id' => 'wilayah']) }}
                    @if($errors->has('wilayah'))
                          <div class="text-danger">
                              {{ $errors->first('wilayah')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('telp', 'Telephon') }}  
                    {{ Form::text('telp',null,['class' => 'form-control']) }}
                    @if($errors->has('telp'))
                          <div class="text-danger">
                              {{ $errors->first('telp')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('tipe', 'Tipe') }}  
                    {{ Form::select('tipe', ['PTR' => 'Kos Putra', 'PUT' => 'Kos Putri','CMP' => 'Kos Campur'], null ,['class' => 'form-control select2']) }}
                    @if($errors->has('tipe'))
                          <div class="text-danger">
                              {{ $errors->first('tipe')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group" >
                    {{ Form::label('fasilitas', 'Fasilitas') }}  
                    <!-- {{ Form::textarea('fasilitas', null ,['class' => 'form-control','rows' => 5]) }} -->
                    <!-- @if($errors->has('fasilitas'))
                          <div class="text-danger">
                              {{ $errors->first('fasilitas')}}
                          </div>
                     @endif -->

                     <div class="scrollable"  style="border:1px solid #ccc; height: 200px; overflow-y: scroll;">
                        @foreach($fasilitas as $fas)
                        <div class="checkbox">
                            <label>
                              &nbsp;<input name="fasilitas[]" value="{{ $fas->id }}" type="checkbox" />&nbsp;{{ $fas->nama }}
                            </label>
                          </div>
                        @endforeach
                      </div>    
                  </div>

                  <div class="form-group">
                    {{ Form::label('deskripsi', 'Deskripsi') }}  
                    {{ Form::textarea('deskripsi', null ,['class' => 'form-control','rows' => 5]) }}
                    @if($errors->has('deskripsi'))
                          <div class="text-danger">
                              {{ $errors->first('deskripsi')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('jml_kamar', 'Jumlah Kamar') }}  
                    {{ Form::number('jml_kamar', null ,['class' => 'form-control']) }}
                    @if($errors->has('jml_kamar'))
                          <div class="text-danger">
                              {{ $errors->first('jml_kamar')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('harga_sewa', 'Harga sewa per bulan') }}  
                    {{ Form::number('harga_sewa', null ,['class' => 'form-control']) }}
                    @if($errors->has('harga_sewa'))
                          <div class="text-danger">
                              {{ $errors->first('harga_sewa')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('foto', 'Foto') }}  
                    {{ Form::file('foto[]', ['class' => 'form-control','multiple' => 'multiple']) }}
                    @if($errors->has('foto'))
                          <div class="text-danger">
                              {{ $errors->first('foto')}}
                          </div>
                     @endif
                  </div>

                  <input type="hidden" name="lat" id="lat" value="-8.22337514570216">
                  <input type="hidden" name="lng" id="lng" value="114.36737010012715">

                  <div class="form-group">
                    <script type="text/javascript" src="http://maps.google.com/maps/api/js?key=AIzaSyAEny_-rEC4vWYBxlfham5LTtvKDuxF8BE"></script>
                    <div id="map" style="width: 100%; height: 500px;"></div>
                    <script>
                     // global "map" variable
                     var map = null;
                     var marker = null;

                     var infowindow = new google.maps.InfoWindow({size: new google.maps.Size(150,50)});

                     // A function to create the marker and set up the event window function
                     function createMarker(map,infowindow,latlng, name, html) {
                         var contentString = html;
                         var marker = new google.maps.Marker({
                             position: latlng,
                             map: map,
                             zIndex: Math.round(latlng.lat()*-100000)<<5
                         });

                         google.maps.event.addListener(marker, 'click', function() {
                             infowindow.setContent(contentString);
                             infowindow.open(map,marker);
                         });

                         google.maps.event.trigger(marker, 'click');
                         return marker;
                     }


                     function initialize() {
                         

                         
                           var myLatLng = {lat: {{ -8.22337514570216 }}, lng: {{ 114.36737010012715 }} };
                           // create the map
                           var myOptions = {
                             zoom: 15,
                             center: new google.maps.LatLng({{ -8.22337514570216 }}, {{ 114.36737010012715 }}),
                             mapTypeControl: true,
                             mapTypeControlOptions: {style: google.maps.MapTypeControlStyle.DROPDOWN_MENU},
                             navigationControl: true,
                             mapTypeId: google.maps.MapTypeId.ROADMAP
                           }

                           map = new google.maps.Map(document.getElementById("map"), myOptions);

                           var marker = new google.maps.Marker({
                               position: myLatLng,
                               map: map,
                               title: 'Hello World!'
                             });


                           google.maps.event.addListener(map, 'click', function() {
                             infowindow.close();
                           });

                           google.maps.event.addListener(map, 'click', function(event) {
                           	//call function to create marker
                               if (marker) {
                                   marker.setMap(null);
                                   marker = null;
                               }

                               marker = createMarker(map,infowindow,event.latLng, "name", "<b>Location</b><br>"+event.latLng);
                               $('#lat').val(event.latLng.lat());
                               $('#lng').val(event.latLng.lng());                             
                       });


                      
                     }
                                     

                   window.onload = initialize;

                   </script>
                  </div>



                </div>

                <div class="card-footer">                  
                  {{ Form::submit('Submit',['class' => 'btn btn-primary']) }}
                </div>
              {{ Form::close() }}  

              
            </div>
            <!-- /.card -->

          </div>
        </div>
      </div>
    </section>
    <script type="text/javascript">     
      $('.select2').select2({theme: 'bootstrap4'})

      
      
      function getKelurahan(kecamatan){
        //   alert(kecamatan);
        $.get( "{{ URL::to('/pemilik/get-kelurahan') }}",
                {kec : kecamatan}
            ).done(function(data){
                $('#wilayah').html(data);
                
        });
      }

      $('#kecamatan').change(function(){
            var kec_id = $(this).val();          
            getKelurahan(kec_id);
      });
    </script>
@endsection
