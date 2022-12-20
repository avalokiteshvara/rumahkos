@inject('helper', 'App\Helpers\HelpersFunction')
@extends('pemilik.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Penyewa</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Home</a></li>
               <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/rumah-kos') }}">Data Rumah Kos</a></li>
               <li class="breadcrumb-item active">Penyewa</li>
            </ol>
         </div>
      </div>
   </div>
   <!-- /.container-fluid -->
</section>
<section class="content">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <div class="alert alert-success" role="alert">
                Data tagihan jatuh tempo maksimal kurang dari 7 hari untuk bulan ini
            </div>
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Penyewa</h3>
                     </div>
                     <div class="col-md-2">
                     <a href="{{ URL::to('/pemilik/riwayat-tagihan-sewa') }}" class="btn btn-primary float-right">Riwayat Bayar</a>  
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Nama Kos</th> 
                           <th>Nama Kamar</th>
                           <th>Nama Penyewa</th>
                           <th>Jatuh Tempo</th>
                           <th>Bulan Sewa</th>
                           <th>Harga Sewa (total)</th>                           
                           <th></th>
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ $d->nama_kos }}</td> 
                           <td>{{ $d->nama_kamar }}</td>
                           <td>{!! $d->nama_penyewa !!}</td>
                           <td>{!! $d->tgl_jatuh_tempo !!}</td>
                           <td>{!! $d->bulan_sewa !!}</td>
                           <td>                           
                              {!! $helper->rupiah($d->harga_total) !!}
                              <br/>                              
                              @if($d->status_bayar == 0)
                              <span class="badge badge-warning">Belum bayar</span>                              
                                 @if($d->hari_jatuh_tempo > 0)
                                 <span class="badge badge-danger">+{{ $d->hari_jatuh_tempo }} Hari</span>
                                 @else
                                    @if($d->hari_jatuh_tempo == 0)
                                    <span class="badge badge-danger">Jatuh tempo !</span>
                                    @else
                                       <span class="badge badge-secondary">-{{ $d->hari_jatuh_tempo }} Hari</span>
                                    @endif      
                                 @endif
                              @else
                              <span class="badge badge-success">Sudah bayar</span>
                              @endif
                           </td>
                           
                           <td>  
                              @if($d->status_bayar == 0)                        
                              <a href="{!! URL::to('/pemilik/tagihan-lunas/' . sha1($d->id)) !!}" class="btn btn-warning btn-xs">Bayar langsung</a>
                              @endif
                           </td>
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
       "columnDefs": [
            { "searchable": false, "targets": 6 }
        ]
     });
   });
</script>
@endsection