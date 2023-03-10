@extends('admin.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Pemilik Kos</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/admin') }}">Home</a></li>
               <li class="breadcrumb-item active">Data Pemilik Kos</li>
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
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Pemilik Kos</h3>
                     </div>
                     <div class="col-md-2">
                        <!-- <a href="{{ URL::to('/pemilik/rumah-kos-tambah') }}" class="btn btn-primary float-right">Tambah Data</a>               -->
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Nama</th>
                           <th>Email</th>
                           <th>Telp</th>
                           <th>Alamat</th>      
                           <th>Verifikasi</th>                                             
                           <!-- <th>#</th> -->
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ ucwords($d->nama) }}</td>
                           <td>{!! $d->email !!}</td>
                           <td>{!! $d->telp !!}</td>
                           <td>{!! $d->alamat !!}</td>                           
                           <td>
                              <ul>
                                 <li>Foto Diri : <a target="_BLANK" href="{{ URL::to('uploads/' . $d->foto) }}">Lihat</a></li>
                                 <li>Foto Ktp : <a target="_BLANK" href="{{ URL::to('uploads/' . $d->ktp_foto) }}">Lihat</a></li>
                                 @if($d->verifikasi === 'P')
                                 
                                 <li>
                                    Status:&nbsp;<span class="badge badge-secondary">Belum verifikasi&nbsp;</span>
                                    [&nbsp;<a href="{!! URL::to('admin/verifikasi-diterima/' . $d->id) !!}" style="color:green">Diterima</a>&nbsp;|&nbsp;<a href="{!! URL::to('admin/verifikasi-ditolak/' . $d->id) !!}" style="color: red;">Ditolak</a>&nbsp;]
                                 </li>
                                 
                                 @elseif($d->verifikasi === 'N')
                                 <li>Status:&nbsp;<span class="badge badge-danger">Ditolak</span></li>
                                 @else
                                 <li>Status:&nbsp;<span class="badge badge-success">Diterima</span></li>
                                 @endif                                 
                              </ul>
                              
                           </td>
                           <!-- <td></td> -->
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
     });
   });
</script>
@endsection