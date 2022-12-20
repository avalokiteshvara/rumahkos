@extends('pemilik.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Edit Data Rumah Kos</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Beranda</a></li>
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos') }}">Data Rumah Kos</a></li>
              <li class="breadcrumb-item active">Edit</li>
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

              @if (count($errors) > 0)
              <div class="alert alert-danger">
                <strong>Whoops!</strong> There were some problems with your input.<br><br>
                <ul>
                  @foreach ($errors->all() as $error)
                      <li>{{ $error }}</li>
                  @endforeach
                </ul>
              </div>
              @endif
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::model($data, ['url' => ['pemilik/rumah-kos-edit', $kos_id],'files' => true]) }}
                <div class="card-body">
                  <div class="form-group">
                    {{ Form::label('nama', 'Nama Rumah Kos') }}  
                    {{ Form::text('nama',$data->nama,['class' => 'form-control']) }}
                    @if($errors->has('nama'))
                          <div class="text-danger">
                              {{ $errors->first('nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('alamat', 'Alamat') }}  
                    {{ Form::text('alamat',$data->alamat,['class' => 'form-control']) }}
                    @if($errors->has('alamat'))
                          <div class="text-danger">
                              {{ $errors->first('alamat')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('kecamatan', 'Kecamatan') }}  
                    {{ Form::select('kecamatan', $kec, substr($data->wilayah,0,8),['class' => 'form-control select2','id' => 'kecamatan']) }}
                    @if($errors->has('kecamatan'))
                          <div class="text-danger">
                              {{ $errors->first('kecamatan')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('wilayah', 'Kelurahan') }}  
                    {{ Form::select('wilayah', [], $data->wilayah,['class' => 'form-control select2','id' => 'kelurahan']) }}
                    @if($errors->has('wilayah'))
                          <div class="text-danger">
                              {{ $errors->first('wilayah')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('telp', 'Telephon') }}  
                    {{ Form::text('telp',$data->telp,['class' => 'form-control']) }}
                    @if($errors->has('telp'))
                          <div class="text-danger">
                              {{ $errors->first('telp')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('tipe', 'Tipe') }}  
                    {{ Form::select('tipe', ['PTR' => 'Kos Putra', 'PUT' => 'Kos Putri','CMP' => 'Kos Campur'], $data->tipe ,['class' => 'form-control select2']) }}
                    @if($errors->has('tipe'))
                          <div class="text-danger">
                              {{ $errors->first('tipe')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('fasilitas', 'Fasilitas') }}  
                    <!-- {{ Form::textarea('fasilitas', $data->fasilitas ,['class' => 'form-control','rows' => 5]) }}
                    @if($errors->has('fasilitas'))
                          <div class="text-danger">
                              {{ $errors->first('fasilitas')}}
                          </div>
                     @endif -->

                     <div class="scrollable"  style="border:1px solid #ccc; height: 200px; overflow-y: scroll;">
                        @foreach($fasilitas as $fas)
                        <div class="checkbox">
                            <label>
                              @if(in_array($fas->id,$selected_fasilitas))
                              &nbsp;<input name="fasilitas[]" value="{{ $fas->id }}" checked type="checkbox" />&nbsp;{{ $fas->nama }}
                              @else
                              &nbsp;<input name="fasilitas[]" value="{{ $fas->id }}" type="checkbox" />&nbsp;{{ $fas->nama }}
                              @endif                              
                            </label>
                          </div>
                        @endforeach
                      </div>    
                  </div>

                  <div class="form-group">
                    {{ Form::label('deskripsi', 'Deskripsi') }}  
                    {{ Form::textarea('deskripsi', $data->deskripsi ,['class' => 'form-control','rows' => 5]) }}
                    @if($errors->has('deskripsi'))
                          <div class="text-danger">
                              {{ $errors->first('deskripsi')}}
                          </div>
                     @endif

                     
                    
                  </div>

                  <div class="form-group">
                    {{ Form::label('jml_kamar', 'Jumlah Kamar') }}  
                    {{ Form::number('jml_kamar', $data->jml_kamar ,['class' => 'form-control']) }}
                    @if($errors->has('jml_kamar'))
                          <div class="text-danger">
                              {{ $errors->first('jml_kamar')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('harga_sewa', 'Harga sewa per bulan') }}  
                    {{ Form::number('harga_sewa', $data->harga_sewa ,['class' => 'form-control']) }}
                    @if($errors->has('harga_sewa'))
                          <div class="text-danger">
                              {{ $errors->first('harga_sewa')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('foto', 'Foto') }}  
                    {{ Form::file('foto[]', ['class' => 'form-control','multiple' => 'multiple']) }}
                  </div>
                  
                      
                  @if(strlen($data->foto) > 0)
                  <h4><i class="glyphicon glyphicon-picture"></i> Display Data Image    </h4>
                  <table class="table table-bordered table-hover table-striped">
                    <thead>
                    <tr>
                      <th>Gambar</th>
                      <th>#</th>
                    </tr>
                    </thead>
                    <tbody>
                      
                      @foreach (explode(',', $data->foto) as $picture)
                      <tr>                      
                        <td> 
                        <img src="{{ asset('/uploads/'. $picture) }}" style="height:120px; width:200px"/>
                        </td>
                        <td><a href="{{ URL::to('pemilik/delete-image/' . sha1($data->id) . '/' . $picture) }}">Hapus</a></td>
                      </tr>    
                                        
                      @endforeach
                      </tbody>
                  </table>
                  <br/>
                      @endif
                        
                   
                  <input type="hidden" name="lat" id="lat" value="{{ $data->lat }}">
                  <input type="hidden" name="lng" id="lng" value="{{ $data->lng }}">

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
                         

                         
                           var myLatLng = {lat: {{ $data->lat }}, lng: {{ $data->lng }} };
                           // create the map
                           var myOptions = {
                             zoom: 15,
                             center: new google.maps.LatLng({{ $data->lat }}, {{ $data->lng }}),
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
                             
                              //  $.post( "{{ URL::to('/pemilik/update-lokasi') }}" ,
                              //   {   kos_id: {{ $data->id }},
                              //       latLng: event.latLng.lat() + '|' + event.latLng.lng(), 
                              //       _token: "{{ csrf_token() }}" 
                              //   }
                              //  );

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

      getKelurahan('{{ substr($data->wilayah,0,8) }}')
      
      function getKelurahan(kecamatan){
        //   alert(kecamatan);
        $.get( "{{ URL::to('/pemilik/get-kelurahan') }}",
                {kec : kecamatan}
            ).done(function(data){
                $('#kelurahan').html(data);
                $('#kelurahan option[value="{{ $data->wilayah }}"]').attr("selected",true);
        });
      }

      $('#kecamatan').change(function(){
            var kec_id = $(this).val();          
            getKelurahan(kec_id);
      });
    </script>
@endsection
